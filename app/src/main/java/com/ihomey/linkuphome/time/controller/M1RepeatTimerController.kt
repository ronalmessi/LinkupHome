package com.ihomey.linkuphome.time.controller

import com.ihomey.linkuphome.chechSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.spp.BluetoothSPP

class M1RepeatTimerController(val device: Device): RepeatTimerController {

    override fun setRepeatTimer(minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int) {
        val timerId = if (isOpenTimer) "01" else "02"
        val commandPrefix = "BF01D101CD08C20601$timerId"+ if (repeatMode > 0) Integer.toHexString(repeatMode + 128) else "00"+ String.format("%02d",hour)+String.format("%02d",minute)+if (isOn) "64" else "00"
        val command = commandPrefix + chechSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.macAddress, decodeHex(command.toUpperCase().toCharArray()), false)
    }

    private fun cancelTimer(device: Device, timerId: String) {
        val commandPrefix = "BF01D101CD04C20602$timerId"
        val command = commandPrefix + chechSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.macAddress, decodeHex(command.toUpperCase().toCharArray()), false)
    }

}