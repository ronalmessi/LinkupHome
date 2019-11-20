package com.ihomey.linkuphome.controller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex


class A2Controller(val device: Device):CommonController,ColorTemperatureController {

    override fun setBrightness(brightness: Int) {
        val commandPrefix="C201F203C2" + if (brightness >= 16) Integer.toHexString(brightness) else "0" + Integer.toHexString(brightness)+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        DataModelApi.sendData(device.instructId, decodeHex((if (isOn) "C201F203C164002816" else "C201F203C10000C416").toCharArray()), false)
    }

    override fun setColorTemperature(colorTemperature: Int) {
        val colorTemperatureValue = ((colorTemperature - 3000) * 100 / (50 * 70))
        val commandPrefix="C201F203C3" + if (colorTemperatureValue >= 16) Integer.toHexString(colorTemperatureValue) else "0" + Integer.toHexString(colorTemperatureValue)+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

}
