package com.ihomey.linkuphome.device1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.base.BaseFragment

import com.ihomey.linkuphome.R
import kotlinx.android.synthetic.main.reset_device_fragment.*

class ResetDeviceFragment : BaseFragment() {

    companion object {
        fun newInstance() = ResetDeviceFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reset_device_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
    }
}
