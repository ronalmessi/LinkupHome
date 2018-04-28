package com.ihomey.linkuphome.listeners

/**
 * Created by Administrator on 2017/6/21.
 */
interface BatteryValueListener {
    fun onBatteryLevelReceived(deviceId: Int, batteryValue: Int)
}