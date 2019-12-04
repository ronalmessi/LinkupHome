package com.ihomey.linkuphome.sigmesh

import android.app.Activity
import com.ihomey.linkuphome.data.entity.Zone


interface Connector {

    fun bind(activity: Activity)

    fun unBind(activity: Activity)

    fun initService(zone: Zone)

    fun startScan()

    fun stopScan()

    fun connect()
}