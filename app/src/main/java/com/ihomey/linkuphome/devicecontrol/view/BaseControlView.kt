package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.listener.DeviceStateChangeListener
import com.ihomey.linkuphome.protocol.sigmesh.MeshInfoListener

abstract class BaseControlView(context: Context,layoutId:Int) {

     var listener:DeviceStateChangeListener?=null

    fun setDeviceStateChangeListener(listener: DeviceStateChangeListener) {
        this.listener = listener
    }

    private var controlView= LayoutInflater.from(context).inflate(layoutId,null)

    fun getControlView():View{
        return controlView
    }

    abstract fun bindTo(device: Device)

}


