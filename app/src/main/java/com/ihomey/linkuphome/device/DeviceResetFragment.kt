package com.ihomey.linkuphome.device

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentDeviceResetBinding


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceResetFragment : BaseFragment() {

    fun newInstance(): DeviceResetFragment {
        return DeviceResetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mViewDataBinding = DataBindingUtil.inflate<FragmentDeviceResetBinding>(inflater, R.layout.fragment_device_reset, container, false)
        mViewDataBinding.handlers = EventHandler()
        return mViewDataBinding.root
    }

    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> (view.context as Activity).onBackPressed()
            }
        }
    }
}