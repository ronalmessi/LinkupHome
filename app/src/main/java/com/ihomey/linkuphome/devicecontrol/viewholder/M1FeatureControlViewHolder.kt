package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import android.widget.ImageButton
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device


class M1FeatureControlViewHolder(parentView: View) : View.OnClickListener {

    private val environmentBtn = parentView.findViewById<ImageButton>(R.id.btn_device_environmental_indicators)

    private val alarmSettingBtn = parentView.findViewById<ImageButton>(R.id.btn_device_alarm_setting)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        environmentBtn.setOnClickListener(this)
        alarmSettingBtn.setOnClickListener(this)
        environmentBtn.visibility = if (device.roomId == 1) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.btn_device_alarm_setting -> Navigation.findNavController(it).navigate(R.id.action_deviceControlFragment_to_alarmListFragment)
                R.id.btn_device_environmental_indicators -> Navigation.findNavController(it).navigate(R.id.action_deviceControlFragment_to_environmentalIndicatorsFragment)
            }
        }
    }
}