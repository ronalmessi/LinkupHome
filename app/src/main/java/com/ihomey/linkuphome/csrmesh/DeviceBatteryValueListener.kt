package com.ihomey.linkuphome.csrmesh

/**
 * Created by Administrator on 2017/6/21.
 */
interface DeviceBatteryValueListener {
    fun onBatteryLevelReceived(deviceId: Int, batteryValue: Int)
}