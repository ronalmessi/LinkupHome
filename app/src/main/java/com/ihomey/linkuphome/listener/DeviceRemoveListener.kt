package com.ihomey.linkuphome.listener

/**
 * Created by Administrator on 2017/6/21.
 */
interface DeviceRemoveListener {
    fun onDeviceRemoved(deviceId: String, uuidHash: Int, success: Boolean)
}