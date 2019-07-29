package com.ihomey.linkuphome.control

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.SleepModeDialogFragment
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.controller.M1Controller

import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.spp.BluetoothSPP
import com.ihomey.linkuphome.zone.DeleteZoneFragment
import kotlinx.android.synthetic.main.m1_control_setting_fragment.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
class M1ControlSettingFragment : BaseFragment() {

    private lateinit var viewModel: HomeActivityViewModel

    private var controller: Controller? = null

    private var mDevice: Device?=null

    companion object {
        fun newInstance() = M1ControlSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.m1_control_setting_fragment,container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(this, Observer<Device> {
            controller = ControllerFactory().createController(it.type)
            mDevice=it
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_sleep_mode.setOnClickListener {
            val dialogFragment = SleepModeDialogFragment()
            dialogFragment.show(fragmentManager, null)
        }
        sb_sleep_mode.setOnCheckedChangeListener { _, isChecked -> controller?.setSleepMode(mDevice?.id,if(isChecked) 1 else 0)}
        sb_gesture_control.setOnCheckedChangeListener { _, isChecked -> controller?.enableGestureControl(mDevice?.id,isChecked)}
        infoTextLayout_setting_syncTime.setOnClickListener { controller?.syncTime(mDevice?.id)}
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
        infoTextLayout_setting_timer.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_m1ControlSettingFragment_to_m1TimerSettingFragment) }
    }

}