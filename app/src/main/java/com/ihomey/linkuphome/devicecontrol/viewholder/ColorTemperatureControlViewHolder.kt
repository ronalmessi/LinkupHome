package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.widget.dashboardview.DashboardView


class ColorTemperatureControlViewHolder(parentView: View) : DashboardView.ColorTemperatureListener {

    private val colorTemperatureView = parentView.findViewById<DashboardView>(R.id.device_dbv_color_temperature)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        colorTemperatureView.setColorTemperatureListener(this)
    }

    override fun onColorTemperatureValueChanged(temperature: Int) {
        LightControllerFactory().createColorTemperatureController(mDevice)?.setColorTemperature(temperature)
    }
}