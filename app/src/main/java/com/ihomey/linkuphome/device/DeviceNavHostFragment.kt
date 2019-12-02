package com.ihomey.linkuphome.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.home.HomeFragment


class DeviceNavHostFragment : BaseNavHostFragment() {

    fun newInstance(): DeviceNavHostFragment {
        return DeviceNavHostFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.navhost_device_fragment, container, false)
    }

    fun getPagePosition(): Int {
        return (parentFragment as HomeFragment).getPagePosition()
    }
}
