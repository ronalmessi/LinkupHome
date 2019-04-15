package com.ihomey.linkuphome.control

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import cn.iclass.guideview.GuideBuilder
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device1.DeviceNavHostFragment
import com.ihomey.linkuphome.device1.ReNameDeviceFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.FragmentBackHandler

import com.ihomey.linkuphome.listener.UpdateDeviceNameListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.widget.RGBCircleView
import com.ihomey.linkuphome.widget.ToggleButtonGroup
import com.ihomey.linkuphome.widget.dashboardview.DashboardView
import kotlinx.android.synthetic.main.zone_fragment.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
abstract class BaseControlFragment : BaseFragment(),FragmentBackHandler, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ToggleButtonGroup.OnCheckedChangeListener, RGBCircleView.ColorValueListener, DashboardView.ColorTemperatureListener {


    private var controller: Controller? = null
    protected lateinit var mControlDevice: SingleDevice
    protected lateinit var mViewModel: HomeActivityViewModel
    protected lateinit var listener: MeshServiceStateListener
    private var guide: Guide? = null
    private var type: Int = -1

    var hasShowRenameDeviceGuide by PreferenceHelper("hasShowRenameDeviceGuide", false)

    abstract fun updateViewData(singleDevice: SingleDevice)

    abstract fun getTitleView(): TextView?


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getCurrentControlDevice().observe(this, Observer<SingleDevice> {
            updateViewData(it)
            getTitleView()?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (!hasShowRenameDeviceGuide) getTitleView()?.let { it1 -> showGuideView(it1) }
                    getTitleView()?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(type!=8){
            parentFragment?.parentFragment?.let { (it as DeviceNavHostFragment).showBottomNavigationBar(false) }
        } else{
            if(parentFragment?.parentFragment is DeviceNavHostFragment){
                parentFragment?.parentFragment?.let { (it as DeviceNavHostFragment).showBottomNavigationBar(false) }
            }
        }
    }

    fun initController(type: Int) {
        this.type = type
        controller = ControllerFactory().createController(type+1)
    }

    override fun onBackPressed(): Boolean {
        return if (guide != null && guide?.isVisible!!) {
            hideGuideView()
            true
        }else{
            false
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onColorValueChanged(time: Int) {
        if (listener.isMeshServiceConnected()) controller?.setLightColor(mControlDevice.instructId, CODE_LIGHT_COLORS[time])
    }

    override fun onColorValueChange(time: Int) {
        if (listener.isMeshServiceConnected()) controller?.setLightColor(mControlDevice.instructId, CODE_LIGHT_COLORS[time])
    }

    override fun onColorTemperatureValueChanged(temperature: Int) {
        if (listener.isMeshServiceConnected()) controller?.setLightColorTemperature(mControlDevice.instructId, temperature)
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean) {
        if (listener.isMeshServiceConnected()) controller?.setLightSpeed(mControlDevice.instructId, position)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (listener.isMeshServiceConnected()) controller?.setLightBright(mControlDevice.instructId, if(type==5||type==9) seekBar.progress.plus(5) else seekBar.progress.plus(15))
        changeDeviceState(mControlDevice,"brightness", seekBar.progress.toString())
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.device_state_cb_power -> {
                if (listener.isMeshServiceConnected()) controller?.setLightPowerState(mControlDevice.instructId, if (isChecked) 1 else 0)
                changeDeviceState(mControlDevice,"on",if (isChecked) "1" else "0")
            }
        }
    }


    inner class ToolBarEventHandler : UpdateDeviceNameListener {
        override fun updateDeviceName(id: Int, newName: String) {
            context?.getIMEI()?.let { it1 ->  mViewModel.changeDeviceName(it1,mControlDevice.zoneId,id,mControlDevice.type,newName).observe(viewLifecycleOwner, Observer<Resource<SingleDevice>> {
                if (it?.status == Status.SUCCESS) {
                    mControlDevice.name = newName
                   updateViewData(mControlDevice)
                }else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })}
        }

        fun onClick(view: View) {
            when (view.id) {
                R.id.iv_back -> Navigation.findNavController(view).popBackStack()
                R.id.btn_device_lighting -> if (listener.isMeshServiceConnected()) controller?.setLightingMode(mControlDevice.instructId)
                R.id.btn_device_scene_setting -> {
                    when (type) {
                        1 -> Navigation.findNavController(view).navigate(R.id.action_r2ControlFragment_to_r2SceneSettingFragment2)
                        3 -> Navigation.findNavController(view).navigate(R.id.action_n1ControlFragment_to_n1SceneSettingFragment)
                        5 -> Navigation.findNavController(view).navigate(R.id.action_v1ControlFragment_to_v1SceneSettingFragment)
                        6 -> Navigation.findNavController(view).navigate(R.id.action_s1ControlFragment_to_r2SceneSettingFragment)
                        8 -> Navigation.findNavController(view).navigate(R.id.action_t1ControlFragment_to_t1SceneSettingFragment)
                        9 -> Navigation.findNavController(view).navigate(R.id.action_v2ControlFragment_to_v2SceneSettingFragment)
                    }
                }
                R.id.btn_device_alarm_setting -> {
                    when (type) {
                        1 -> Navigation.findNavController(view).navigate(R.id.action_r2ControlFragment_to_timerSettingFragment2)
                        2 -> Navigation.findNavController(view).navigate(R.id.action_a2ControlFragment_to_timerSettingFragment)
                        3 -> Navigation.findNavController(view).navigate(R.id.action_n1ControlFragment_to_timerSettingFragment)
                        5 -> Navigation.findNavController(view).navigate(R.id.action_v1ControlFragment_to_repeatTimerSettingFragment)
                        6 -> Navigation.findNavController(view).navigate(R.id.action_s1ControlFragment_to_timerSettingFragment)
                        7 -> Navigation.findNavController(view).navigate(R.id.action_s2ControlFragment_to_timerSettingFragment)
                        8 -> Navigation.findNavController(view).navigate(R.id.action_t1ControlFragment_to_timerSettingFragment)
                        9 -> Navigation.findNavController(view).navigate(R.id.action_v2ControlFragment_to_v2SceneSettingFragment)
                    }
                }
                R.id.tv_title -> {
                    hideGuideView()
                    val dialog = ReNameDeviceFragment()
                    val bundle = Bundle()
                    bundle.putInt("deviceId", mControlDevice.id)
                    bundle.putString("deviceName", mControlDevice.name)
                    dialog.arguments = bundle
                    dialog.setUpdateZoneNameListener(this)
                    dialog.show(fragmentManager, "ReNameDeviceFragment")
                }
            }
        }
    }

    private fun showGuideView(view: View) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingLeft(context?.resources?.getDimension(R.dimen._27sdp)?.toInt()!!)
                .setHighTargetPaddingRight(context?.resources?.getDimension(R.dimen._27sdp)?.toInt()!!)
                .setHighTargetPaddingBottom(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetPaddingTop(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetMarginTop(getMarginTop(view)+context?.resources?.getDimension(R.dimen._13sdp)?.toInt()!!)
                .setAutoDismiss(false)
                .setOverlayTarget(false)
                .setOutsideTouchable(false)
        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {
                hasShowRenameDeviceGuide = true
            }

            override fun onDismiss() {}
        })
        builder.addComponent(object : Component {
            override fun getView(inflater: LayoutInflater): View {
                return inflater.inflate(R.layout.view_guide_device_rename, null)
            }

            override fun getAnchor(): Int {
                return Component.ANCHOR_BOTTOM
            }

            override fun getFitPosition(): Int {
                return Component.FIT_CENTER
            }

            override fun getXOffset(): Int {
                return 0
            }

            override fun getYOffset(): Int {
                return context?.resources?.getDimension(R.dimen._8sdp)?.toInt()!!
            }

        })
        guide = builder.createGuide()
        guide?.setShouldCheckLocInWindow(true)
        guide?.show(activity)
    }

    private fun getMarginTop(view: View): Int {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        return loc[1]
    }

    private fun hideGuideView() {
        if (guide != null && guide?.isVisible!!) {
            guide?.dismiss()
        }
    }

    private fun changeDeviceState(singleDevice: SingleDevice,key:String,value:String){
        context?.getIMEI()?.let { it1 ->  mViewModel.changeDeviceState(it1,singleDevice.id,key,value).observe(viewLifecycleOwner, Observer<Resource<SingleDevice>> {
            if (it?.status == Status.SUCCESS) {

            }else if (it?.status == Status.ERROR) {
                it.message?.let { it2 -> activity?.toast(it2) }
            }
        })}
    }
}