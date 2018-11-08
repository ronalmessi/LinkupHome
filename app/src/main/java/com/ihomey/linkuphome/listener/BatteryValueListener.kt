package com.ihomey.linkuphome.listener

/**
 * Created by Administrator on 2017/6/21.
 */
interface BatteryValueListener {
    fun onBatteryLevelReceived(deviceId: Int, batteryValue: Int)
}