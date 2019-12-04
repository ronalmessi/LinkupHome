package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation
import com.ihomey.linkuphome.sigmesh.CSRMeshServiceManager
import com.ihomey.linkuphome.sigmesh.DeviceBatteryValueListener
import com.ihomey.linkuphome.widget.SingleSelectToggleGroup
import com.ihomey.linkuphome.widget.ToggleButtonGroup
import kotlin.math.ceil


class DeviceBatteryViewHolder(private val parentView: View) : DeviceBatteryValueListener {

    private val batteryView = parentView.findViewById<ImageView>(R.id.device_iv_battery)

    fun bindTo(device: Device) {
        CSRMeshServiceManager.getInstance().getBatteryLevelStr(device,this)
    }

    override fun onBatteryLevelReceived(deviceId: Int, batteryValue: Int) {
        if (deviceId > 32768) {
            batteryView.visibility = View.VISIBLE
            batteryView.setImageResource(AppConfig.BATTERY_LEVEL_ICON[ceil(batteryValue / 20.0).toInt()])
        }
    }
}