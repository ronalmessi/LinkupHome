package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.viewholder.*


class A2ControlView(val layoutId:Int, val context:Context): BaseControlView(context,layoutId) {

    override fun bindTo(device: Device) {
        val parentView=getControlView()
        CommonControlViewHolder(parentView).bindTo(device)
        ColorTemperatureControlViewHolder(parentView).bindTo(device)
        SwitchTimerControlViewHolder(parentView).bindTo(device)
    }
}