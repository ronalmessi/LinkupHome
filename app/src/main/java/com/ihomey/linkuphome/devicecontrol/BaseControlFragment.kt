package com.ihomey.linkuphome.devicecontrol

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import cn.iclass.guideview.GuideBuilder
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.widget.RGBCircleView
import com.ihomey.linkuphome.widget.ToggleButtonGroup
import com.ihomey.linkuphome.widget.dashboardview.DashboardView


/**
 * Created by dongcaizheng on 2018/4/15.
 */
abstract class BaseControlFragment : BaseFragment(), FragmentBackHandler, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ToggleButtonGroup.OnCheckedChangeListener, RGBCircleView.ColorValueListener, DashboardView.ColorTemperatureListener {

    protected lateinit var mControlDevice: Device
    protected lateinit var mViewModel: HomeActivityViewModel
    protected lateinit var listener: MeshServiceStateListener
    private var guide: Guide? = null
    private var type: Int = -1

    var hasShowRenameDeviceGuide by PreferenceHelper("hasShowRenameDeviceGuide", false)

    abstract fun updateViewData(device: Device)

    abstract fun getTitleView(): TextView?


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getCurrentControlDevice().observe(this, Observer<Device> {
            updateViewData(it)
            getTitleView()?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (!hasShowRenameDeviceGuide) getTitleView()?.let { it1 -> showGuideView(it1) }
                    getTitleView()?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
        })
    }

    override fun onBackPressed(): Boolean {
        return if (guide != null && guide?.isVisible!!) {
            hideGuideView()
            true
        } else {
            false
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onColorValueChanged(time: Int) {
        LightControllerFactory().createColorController(mControlDevice)?.setColor(AppConfig.RGB_COLOR_POSITION[time])
    }

    override fun onColorValueChange(time: Int) {
        LightControllerFactory().createColorController(mControlDevice)?.setColor(AppConfig.RGB_COLOR_POSITION[time])
    }

    override fun onColorTemperatureValueChanged(temperature: Int) {
        LightControllerFactory().createColorTemperatureController(mControlDevice)?.setColorTemperature(temperature)
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean) {
        LightControllerFactory().createColorController(mControlDevice)?.setCycleMode(position)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        LightControllerFactory().createCommonController(mControlDevice)?.setBrightness( if (type == 6 || type == 10) seekBar.progress else seekBar.progress.plus(15))
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.device_state_cb_power -> {
                LightControllerFactory().createCommonController(mControlDevice)?.setOnOff(isChecked)
                changeDeviceState(mControlDevice, "on", if (isChecked) "1" else "0")
            }
        }
    }


//    inner class ToolBarEventHandler : UpdateDeviceNameListener {
//        override fun updateDeviceName(id: String, newName: String) {
//            if (mControlDevice.type == 0) {
////                mViewModel.changeDeviceName(id, newName)
//                mControlDevice.name = newName
//                updateViewData(mControlDevice)
//            } else {
//                context?.getIMEI()?.let { it1 ->
//                    mViewModel.changeDeviceName(it1, mControlDevice.zoneId, id, mControlDevice.type, newName).observe(viewLifecycleOwner, Observer<Resource<Device>> {
//                        if (it?.status == Status.SUCCESS) {
//                            mControlDevice.name = newName
//                            updateViewData(mControlDevice)
//                        } else if (it?.status == Status.ERROR) {
//                            it.message?.let { it2 -> activity?.toast(it2) }
//                        }
//                    })
//                }
//            }
//        }
//
//        fun onClick(view: View) {
//            when (view.id) {
//
//                R.id.tv_title -> {
////                    hideGuideView()
////                    val dialog = ReNameDeviceFragment()
////                    val bundle = Bundle()
////                    bundle.putString("deviceId", mControlDevice.id)
////                    bundle.putString("deviceName", mControlDevice.name)
////                    dialog.arguments = bundle
////                    dialog.setUpdateZoneNameListener(this)
////                    dialog.show(fragmentManager, "ReNameDeviceFragment")
//                }
//            }
//        }
//    }

    private fun showGuideView(view: View) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingLeft(context?.resources?.getDimension(R.dimen._27sdp)?.toInt()!!)
                .setHighTargetPaddingRight(context?.resources?.getDimension(R.dimen._27sdp)?.toInt()!!)
                .setHighTargetPaddingBottom(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetPaddingTop(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetMarginTop(getMarginTop(view) + context?.resources?.getDimension(R.dimen._13sdp)?.toInt()!!)
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

    private fun changeDeviceState(device: Device, key: String, value: String) {
        updateState(device, key, value)
        context?.getIMEI()?.let { it1 ->
            mViewModel.changeDeviceState(it1, device.id, key, value).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                if (it?.status == Status.SUCCESS) {

                } else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

    private fun updateState(device: Device, key: String, value: String) {
        if (TextUtils.equals("brightness", key)) {
            val deviceState = device.parameters
            deviceState?.let {
                it.brightness = value.toInt()
                mViewModel.updateDeviceState(device, it)
            }
        } else {
            val deviceState = device.parameters
            deviceState?.let {
                it.on = value.toInt()
                mViewModel.updateRoomAndDeviceState(device, it)
            }
        }
    }
}