package com.ihomey.linkuphome.devicecontrol.controller.repeatcontroller

import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.devicecontrol.controller.RepeatTimerController
import com.ihomey.linkuphome.protocol.spp.BluetoothSPP
import java.util.*

class M1RepeatTimerController(val device: Device): RepeatTimerController {

    override fun syncTime() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val commandPrefix = "BF01D101CD09C301"+ String.format("%02d",(calendar.get(Calendar.YEAR) % 2000))+ String.format("%02d",(calendar.get(Calendar.MONTH) + 1))+ String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH))+ String.format("%02d",(calendar.get(Calendar.DAY_OF_WEEK) - 1))+ String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))+ String.format("%02d",calendar.get(Calendar.MINUTE))+ String.format("%02d",calendar.get(Calendar.SECOND))
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toUpperCase().toCharArray()), false)
    }

    override fun setRepeatTimer(minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int) {
        val timerId = if (isOpenTimer) "02" else "01"
        if(isOn){
            val commandPrefix = "BF01D101CD08C20601$timerId"+ (if (repeatMode > 0) Integer.toHexString(repeatMode + 128) else "00")+ String.format("%02d",hour)+String.format("%02d",minute)+ (if (isOpenTimer) "64" else "00")
            val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
            BluetoothSPP.getInstance().send(device.id, decodeHex(command.toUpperCase().toCharArray()), false)
        }else{
            cancelTimer(device, timerId)
        }
    }

    private fun cancelTimer(device: Device, timerId: String) {
        val commandPrefix = "BF01D101CD04C20602$timerId"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toUpperCase().toCharArray()), false)
    }
}