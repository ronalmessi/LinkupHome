package com.ihomey.linkuphome.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.databinding.T1ControlRgbFragmentBinding

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class RgbControlFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: T1ControlRgbFragmentBinding

    fun newInstance(): RgbControlFragment {
        return RgbControlFragment()
    }


    override fun getTitleView(): TextView? {
        return null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.t1_control_rgb_fragment, container, false)
        mViewDataBinding.handlers = ToolBarEventHandler()
        mViewDataBinding.root.setOnClickListener { (parentFragment as M1ControlFragment).hideCyclingSstgSpeedView() }
        return mViewDataBinding.root
    }

    override fun updateViewData(device: Device) {
        mViewDataBinding.control = device
        mControlDevice = device
//        mViewDataBinding.deviceColorRgbCv.setCurrentRadian(mControlDevice.state.colorPosition)
        mViewDataBinding.deviceColorRgbCv.setColorValueListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceColorRgbCv.setColorValueListener(null)
    }

}