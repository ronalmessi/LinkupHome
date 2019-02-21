package com.ihomey.linkuphome.control1

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.navigation.Navigation
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.UpdateDeviceNameListener
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.widget.RGBCircleView
import com.ihomey.linkuphome.widget.ToggleButtonGroup
import com.ihomey.linkuphome.widget.dashboardview.DashboardView
import cn.iclass.guideview.GuideBuilder
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.device1.ReNameDeviceFragment


/**
 * Created by dongcaizheng on 2018/4/15.
 */
abstract class BaseControlFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ToggleButtonGroup.OnCheckedChangeListener, RGBCircleView.ColorValueListener, DashboardView.ColorTemperatureListener, View.OnClickListener {


    private var controller: Controller? = null
    protected lateinit var mControlDevice: SingleDevice
    protected lateinit var mViewModel: HomeActivityViewModel
    protected lateinit var listener: MeshServiceStateListener
    private var guide: Guide? = null

    var hasShowRenameDeviceGuide by PreferenceHelper("hasShowRenameDeviceGuide", false)

    abstract fun updateViewData(singleDevice: SingleDevice)

    abstract fun getTitleView(): TextView


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getCurrentControlDevice().observe(this, Observer<SingleDevice> {
            updateViewData(it)
            getTitleView().viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (!hasShowRenameDeviceGuide) showGuideView(getTitleView())
                    getTitleView().viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(false)
    }

    fun initController(type: Int) {
        controller = ControllerFactory().createController(type)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onColorValueChanged(time: Int) {
        if (listener.isMeshServiceConnected()) controller?.setLightColor(mControlDevice.id, CODE_LIGHT_COLORS[time])
        mControlDevice.state.colorPosition = (time * 151 * (2 * Math.PI) / 3600).toFloat()
        mControlDevice.state.changeMode = -1
        mControlDevice.state.light = 0
        mViewModel.updateDevice(mControlDevice)
    }

    override fun onColorValueChange(time: Int) {
        if (listener.isMeshServiceConnected()) controller?.setLightColor(mControlDevice.id, CODE_LIGHT_COLORS[time])
        mControlDevice.state.colorPosition = (time * 151 * (2 * Math.PI) / 3600).toFloat()
        mControlDevice.state.changeMode = -1
        mControlDevice.state.light = 0
        mViewModel.updateDevice(mControlDevice)
    }

    override fun onColorTemperatureValueChanged(temperature: Int) {
        if (listener.isMeshServiceConnected()) controller?.setLightColorTemperature(mControlDevice.id, temperature)
        mControlDevice.state.colorTemperature = temperature
        mViewModel.updateDevice(mControlDevice)
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean) {
        if (listener.isMeshServiceConnected()) controller?.setLightSpeed(mControlDevice.id, position)
        mControlDevice.state.changeMode = position
        mControlDevice.state.light = 0
        mViewModel.updateDevice(mControlDevice)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (listener.isMeshServiceConnected()) controller?.setLightBright(mControlDevice.id, seekBar.progress.plus(15))
        mControlDevice.state.brightness = seekBar.progress
        mViewModel.updateDevice(mControlDevice)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.device_state_cb_power -> {
                mControlDevice.state.on = if (isChecked) 1 else 0
                if (listener.isMeshServiceConnected()) controller?.setLightPowerState(mControlDevice.id, if (isChecked) 1 else 0)
            }
        }
        mViewModel.updateDevice(mControlDevice)
    }


    inner class ToolBarEventHandler : UpdateDeviceNameListener {
        override fun updateDeviceName(id: Int, newName: String) {
            mControlDevice.name = newName
            mViewModel.updateDeviceName(id, newName)
            updateViewData(mControlDevice)
        }

        fun onClick(view: View) {
            when (view.id) {
                R.id.iv_back -> Navigation.findNavController(view).popBackStack()
                R.id.btn_device_lighting -> {
                    mControlDevice.state.light = 1
                    mControlDevice.state.changeMode = -1
                    if (listener.isMeshServiceConnected()) controller?.setLightingMode(mControlDevice.id)
                    mViewModel.updateDevice(mControlDevice)
                }
                R.id.btn_device_scene_setting -> {
                    Navigation.findNavController(view).navigate(R.id.action_r2ControlFragment_to_r2SceneSettingFragment2)
                }
                R.id.btn_device_alarm_setting -> {
                    Navigation.findNavController(view).navigate(R.id.action_r2ControlFragment_to_timerSettingFragment2)
                }
                R.id.tv_title -> {
                    if (guide != null && guide?.isVisible!!) {
                        guide?.dismiss()
                    } else {
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
    }

    private fun showGuideView(view: View) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingLeft(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingRight(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingBottom(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetPaddingTop(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setOverlayTarget(false)
                .setOutsideTouchable(true)
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

}