package com.ihomey.linkuphome.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.databinding.T1ControlCctFragmentBinding

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class CctControlFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: T1ControlCctFragmentBinding

    fun newInstance(): CctControlFragment {
        return CctControlFragment()
    }


    override fun getTitleView(): TextView? {
        return  null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.t1_control_cct_fragment, container, false)
        initController(8)
        mViewDataBinding.handlers = ToolBarEventHandler()
        return mViewDataBinding.root
    }

    override fun updateViewData(device: Device) {
        mViewDataBinding.control = device
        mControlDevice = device
//        mViewDataBinding.deviceColorRgbCv.setColorTemperature(mControlDevice.state.colorTemperature)
        mViewDataBinding.deviceColorRgbCv.setColorTemperatureListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceColorRgbCv.setColorTemperatureListener(null)
    }

}