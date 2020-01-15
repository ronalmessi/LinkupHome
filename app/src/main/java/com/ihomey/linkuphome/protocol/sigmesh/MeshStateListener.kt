package com.ihomey.linkuphome.protocol.sigmesh


/**
 * Created by Administrator on 2017/6/21.
 */

interface MeshStateListener {
    fun onDeviceStateChanged(name: String,isConnected:Boolean)

    fun onDeviceStateChanged(isConnected:Boolean,macAddress:String)
}