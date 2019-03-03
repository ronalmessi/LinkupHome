package com.ihomey.linkuphome.listener

import com.ihomey.linkuphome.listeners.BatteryValueListener

/**
 * Created by Administrator on 2017/6/21.
 */
interface MeshServiceStateListener {
    fun isMeshServiceConnected(): Boolean
    fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener)
}