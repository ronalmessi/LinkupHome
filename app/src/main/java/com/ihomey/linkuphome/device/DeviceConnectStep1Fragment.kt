package com.ihomey.linkuphome.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.databinding.FragmentDeviceConnectStep1Binding
import com.ihomey.linkuphome.listener.IFragmentStackHolder

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceConnectStep1Fragment : BaseFragment() {

    private lateinit var mViewDataBinding: FragmentDeviceConnectStep1Binding

    fun newInstance(categoryType: Int): DeviceConnectStep1Fragment {
        val deviceConnectFragment = DeviceConnectStep1Fragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        deviceConnectFragment.arguments = bundle
        return deviceConnectFragment
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
                    if (parentFragment is IFragmentStackHolder) {
                        val fsh = parentFragment as IFragmentStackHolder
                        fsh.replaceFragment(R.id.inner_frag_device_connect_container, DeviceConnectStep2Fragment().newInstance(arguments?.getInt("categoryType", -1)!!, false))
                    }
                }
                R.id.btn_device_connect_reset -> (activity as IFragmentStackHolder).replaceFragment(R.id.container, DeviceResetFragment().newInstance(arguments?.getInt("categoryType", -1)!!))
            }
        }
    }
}