package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.ihomey.linkuphome.data.entity.Device

abstract class BaseControlView(context: Context,layoutId:Int) {

    private var controlView= LayoutInflater.from(context).inflate(layoutId,null)

    fun getControlView():View{
        return controlView
    }

    abstract fun bindTo(device: Device)

}


