package com.ihomey.linkuphome.control

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentControlBedBinding
import com.ihomey.linkuphome.databinding.FragmentControlBedRgbBinding
import com.ihomey.linkuphome.databinding.FragmentControlLawnBinding

class BedControlRGBFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: FragmentControlBedRgbBinding

    fun newInstance(): BedControlRGBFragment {
        return BedControlRGBFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initController(5)
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_bed_rgb, container, false)
        mViewDataBinding.deviceStateCbSetting.setOnClickListener(this)
        return mViewDataBinding.root
    }

    override fun updateViewData(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            mViewDataBinding.control = controlDevice
            mControlDevice = controlDevice
            if (mControlDevice != null) {
                mViewDataBinding.deviceColorRcv.setCurrentRadian(mControlDevice!!.state.colorPosition)
            }
            mViewDataBinding.deviceColorRcv.setColorValueListener(this)
            mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
            mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
        } else {
            mViewDataBinding.control = null
            mControlDevice = null
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            try {
                updateViewData(mControlDevice)
            }catch (e:Exception){

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceColorRcv.setColorValueListener(null)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
    }
}