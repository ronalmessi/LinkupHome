package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.SeekBar
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory


class CommonControlViewHolder(parentView: View) : CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private val onOffCheckbox = parentView.findViewById<CheckBox>(R.id.device_state_cb_power)
    private val brightnessSeekBar = parentView.findViewById<SeekBar>(R.id.device_seek_bar_brightness)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        onOffCheckbox.isChecked = (device.parameters?.on == 1)
        onOffCheckbox.setOnCheckedChangeListener(this)
        brightnessSeekBar.max = getMaxBrightness(device)
        brightnessSeekBar.setOnSeekBarChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        LightControllerFactory().createCommonController(mDevice)?.setOnOff(isChecked)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        LightControllerFactory().createCommonController(mDevice)?.setBrightness(seekBar.progress)
    }

    private fun getMaxBrightness(device: Device): Int {
        return when (device.type) {
            3,8 -> 240
            6 -> if (device.pid != 0) 49514 else 22
            10 -> 22
            else -> 85
        }
    }

    private fun getBaseBrightness(device: Device): Int {
        return when (device.type) {
            6 -> if (device.pid != 0) 16021 else 10
            10 ->  10
            else -> 15
        }
    }
}