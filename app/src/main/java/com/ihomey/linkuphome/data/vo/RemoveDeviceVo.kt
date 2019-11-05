package com.ihomey.linkuphome.data.vo

import com.ihomey.linkuphome.listener.DeviceRemoveListener

data class RemoveDeviceVo(val deviceId: String, val deviceInstructId: Int, val devicePId: Int, val deviceRemoveListener: DeviceRemoveListener)