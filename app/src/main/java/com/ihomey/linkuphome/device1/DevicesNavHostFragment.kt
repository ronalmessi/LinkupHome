package com.ihomey.linkuphome.device1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.listener.FragmentBackHandler


class DevicesNavHostFragment : BaseFragment(),FragmentBackHandler {


    fun newInstance(): DevicesNavHostFragment {
        return DevicesNavHostFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.devices_navhost_fragment, container, false)
    }

    override fun onBackPressed(): Boolean {
        return handleBackPress(childFragmentManager.fragments[0])
    }
}
