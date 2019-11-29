package com.ihomey.linkuphome.listener

/**
 * Created by Administrator on 2017/6/21.
 */
interface MeshServiceStateListener {
    fun isMeshServiceConnected(): Boolean
    fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener)
}