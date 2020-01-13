package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.viewholder.ColorTemperatureControlViewHolder
import com.ihomey.linkuphome.devicecontrol.viewholder.CommonControlViewHolder
import com.ihomey.linkuphome.devicecontrol.viewholder.SwitchTimerControlViewHolder


class A2ControlView(val layoutId:Int, val context:Context): BaseControlView(context,layoutId) {

    override fun bindTo(device: Device) {
        val parentView=getControlView()
        CommonControlViewHolder(parentView).bindTo(device,listener)
        ColorTemperatureControlViewHolder(parentView).bindTo(device)
        SwitchTimerControlViewHolder(parentView).bindTo(device)
    }
}