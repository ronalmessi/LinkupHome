package com.ihomey.linkuphome.listeners

import com.ihomey.linkuphome.listener.BatteryValueListener

/**
 * Created by Administrator on 2017/6/21.
 */
interface MeshServiceStateListener {
    fun isMeshServiceConnected(): Boolean
    fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener)
}