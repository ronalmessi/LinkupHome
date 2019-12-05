package com.ihomey.linkuphome.protocol.sigmesh


/**
 * Created by Administrator on 2017/6/21.
 */

interface MeshStateListener {
    fun onDeviceConnected(name: String)
    fun onDeviceDisConnected(name: String)
}