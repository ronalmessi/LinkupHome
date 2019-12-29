package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.viewholder.*


class V2ControlView(val layoutId:Int, val context:Context): BaseControlView(context,layoutId) {

    override fun bindTo(device: Device) {
        val parentView=getControlView()
        CommonControlViewHolder(parentView).bindTo(device)
        ColorControlViewHolder(parentView).bindTo(device)
        RGBrControlViewHolder(parentView).bindTo(device)
        SceneControlViewHolder(parentView).bindTo(device)
    }
}