package com.ihomey.linkuphome.sigmesh

import android.app.Activity
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Zone


interface Connector {

    fun bind(activity: Activity)

    fun unBind(activity: Activity)

    fun initService(zone: Zone)

    fun startScan()

    fun stopScan()

    fun associateDevice(device:Device,listener: MeshDeviceAssociateListener?)

    fun resetDevice(device:Device,listener: MeshDeviceRemoveListener?)

    fun isConnected():Boolean

    fun connect()
}