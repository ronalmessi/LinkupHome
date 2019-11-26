package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.widget.BedRGBCircleView
import com.ihomey.linkuphome.widget.RGBCircleView


class RGBrControlViewHolder(private val parentView: View) : RGBCircleView.ColorValueListener{

    private val rgbColorView = parentView.findViewById<BedRGBCircleView>(R.id.device_color_rgb_cv)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        rgbColorView.setColorValueListener(this)
    }

    override fun onColorValueChanged(time: Int) {
        LightControllerFactory().createColorController(mDevice)?.setColor(AppConfig.RGB_COLOR_POSITION[time])
    }

    override fun onColorValueChange(time: Int) {
        LightControllerFactory().createColorController(mDevice)?.setColor(AppConfig.RGB_COLOR_POSITION[time])
    }

}