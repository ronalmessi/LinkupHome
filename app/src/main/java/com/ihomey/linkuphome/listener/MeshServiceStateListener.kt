package com.ihomey.linkuphome.listeners

/**
 * Created by Administrator on 2017/6/21.
 */
interface MeshServiceStateListener {
    fun isMeshServiceConnected(): Boolean
    fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener)
}