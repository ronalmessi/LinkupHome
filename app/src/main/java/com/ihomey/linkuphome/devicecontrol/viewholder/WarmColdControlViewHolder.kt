package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.widget.BedWarmColdCircleView
import com.ihomey.linkuphome.widget.dashboardview.DashboardView


class WarmColdControlViewHolder(parentView: View) : DashboardView.ColorTemperatureListener {

    private val colorTemperatureView = parentView.findViewById<BedWarmColdCircleView>(R.id.device_color_rgb_cv)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        colorTemperatureView.setColorTemperatureListener(this)
    }

    override fun onColorTemperatureValueChanged(temperature: Int) {
        LightControllerFactory().createColorTemperatureController(mDevice)?.setColorTemperature(temperature)
    }
}