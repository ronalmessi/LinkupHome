package com.ihomey.linkuphome.data.vo

import com.ihomey.linkuphome.listener.DeviceRemoveListener

data class RemoveDeviceVo(val deviceId:String,val deviceInstructId:Int,val deviceRemoveListener:DeviceRemoveListener)