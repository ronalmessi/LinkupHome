package com.ihomey.linkuphome.switchtimer.controller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.getPeriodMinute

class N1TimerController(val device: Device): TimerController {

    override fun setTimer(minute: Int, hour: Int,isOpenTimer: Boolean, isOn: Boolean) {
        val commandPrefix="C201F104C5"+(if (isOn) "64" else "00") + String.format("%04x", getPeriodMinute(hour,minute))
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }


}