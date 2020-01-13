package com.ihomey.linkuphome.listener

import com.ihomey.linkuphome.data.entity.Device

/**
 * Created by Administrator on 2017/6/21.
 */

interface DeviceStateChangeListener {
    fun onDeviceStateChange(device: Device, key: String, value: String)
}