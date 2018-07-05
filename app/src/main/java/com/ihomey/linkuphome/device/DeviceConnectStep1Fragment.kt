package com.ihomey.linkuphome.device

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentDeviceConnectStep1Binding
import com.ihomey.linkuphome.listener.IDeviceConnectStepListener


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceConnectStep1Fragment : BaseFragment() {

    private lateinit var mViewDataBinding: FragmentDeviceConnectStep1Binding

    fun newInstance(categoryType: Int): DeviceConnectStep1Fragment {
        val addProductFragment = DeviceConnectStep1Fragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        addProductFragment.arguments = bundle
        return addProductFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_connect_step1, container, false)
        mViewDataBinding.handlers = EventHandler()
        return mViewDataBinding.root
    }


    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.btn_device_connect_next_step -> {
                    val iDeviceConnectStepListener = parentFragment as IDeviceConnectStepListener
                    iDeviceConnectStepListener.setStep(1)
                }
                R.id.btn_device_connect_reset -> {
                    val iDeviceConnectStepListener = parentFragment as IDeviceConnectStepListener
                    iDeviceConnectStepListener.goToDeviceResetFragment()
                }
            }
        }
    }
}