package com.ihomey.linkuphome.controller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex



class C3Controller(val device: Device):CommonController,ColorController {

    override fun setBrightness(brightness: Int) {
        val commandPrefix="C101F1C003C2" + if (brightness >= 16) Integer.toHexString(brightness) else "0" + Integer.toHexString(brightness)+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(8)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        DataModelApi.sendData(device.instructId, decodeHex((if (isOn) "C101F1C003C164002816" else "C101F1C003C10000C416").toCharArray()), false)
    }

    override fun setColor(colorValue: String) {
        val commandPrefix="C101F1C003C3" + colorValue+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(8)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setLightingMode() {
        val commandPrefix="C101F1C003C3" +"F100"
        val command = commandPrefix + checkSum(commandPrefix.substring(8)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setCycleMode(cycleSpeed: Int) {
        val commandPrefix="C101F1C003C401F" + + (3 - cycleSpeed)
        val command = commandPrefix + checkSum(commandPrefix.substring(8)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }
}
