package com.ihomey.linkuphome.sigmesh


/**
 * Created by Administrator on 2017/6/21.
 */

interface MeshStateListener {
    fun onDeviceConnected(name: String)
    fun onDeviceDisConnected(name: String)
}