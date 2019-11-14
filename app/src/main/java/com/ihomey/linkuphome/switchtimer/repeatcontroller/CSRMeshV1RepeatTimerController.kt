package com.ihomey.linkuphome.switchtimer.repeatcontroller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import java.util.*

class CSRMeshV1RepeatTimerController(val device: Device): RepeatTimerController {

    override fun syncTime() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val commandPrefix="C201F304F2"+String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))+String.format("%02d", calendar.get(Calendar.MINUTE))+String.format("%02d",calendar.get(Calendar.SECOND))
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setRepeatTimer(minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int) {
        val commandPrefix="C201F304"+ (if(isOpenTimer) "C5" else "C6")+(if (isOn) { if (repeatMode==1000) "80" else "00" } else "FF")+ (if (!isOn) "0000" else (String.format("%02d",hour)+String.format("%02d",minute)))
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

}