package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import androidx.fragment.app.Fragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.viewholder.ColorControlViewHolder
import com.ihomey.linkuphome.devicecontrol.viewholder.CommonControlViewHolder
import com.ihomey.linkuphome.devicecontrol.viewholder.RGBrControlViewHolder


class C3ControlView(val layoutId:Int,val context:Context): BaseControlView(context,layoutId) {

    override fun bindTo(device: Device) {
        val parentView=getControlView()
        CommonControlViewHolder(parentView).bindTo(device)
        ColorControlViewHolder(parentView).bindTo(device)
        RGBrControlViewHolder(parentView).bindTo(device)
    }
}