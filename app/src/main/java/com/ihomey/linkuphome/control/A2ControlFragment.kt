package com.ihomey.linkuphome.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.databinding.A2ControlFragmentBinding

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class A2ControlFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: A2ControlFragmentBinding

    fun newInstance(): A2ControlFragment {
        return A2ControlFragment()
    }


    override fun getTitleView(): TextView {
        return  mViewDataBinding.tvTitle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.a2_control_fragment, container, false)
        mViewDataBinding.handlers = ToolBarEventHandler()
        mViewDataBinding.deviceSeekBarBrightness.max=240
        return mViewDataBinding.root
    }

    override fun updateViewData(device: Device) {
        mViewDataBinding.control = device
        mControlDevice = device
        mViewDataBinding.deviceStateCbPower.isChecked=(device.parameters?.on==1)
        device.parameters?.brightness?.let { mViewDataBinding.deviceSeekBarBrightness.progress=it}
//        mViewDataBinding.deviceDbvColorTemperature.currentColorTemperature = mControlDevice.state.colorTemperature
        mViewDataBinding.deviceDbvColorTemperature.setColorTemperatureListener(this)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
        mViewDataBinding.deviceDbvColorTemperature.setColorTemperatureListener(null)
    }

}