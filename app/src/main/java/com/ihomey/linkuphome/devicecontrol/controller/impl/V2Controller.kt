package com.ihomey.linkuphome.devicecontrol.controller.impl

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.devicecontrol.controller.ColorController
import com.ihomey.linkuphome.devicecontrol.controller.CommonController
import com.ihomey.linkuphome.devicecontrol.controller.SceneController


class V2Controller(val device: Device): CommonController, ColorController, SceneController {

    override fun setBrightness(brightness: Int) {
        val commandPrefix="C201F303C2" + (if (brightness >= 6) Integer.toHexString(brightness+10) else "0" + Integer.toHexString(brightness+10))+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        DataModelApi.sendData(device.instructId, decodeHex((if (isOn) "C201F303C164002816" else "C201F303C10000C416").toCharArray()), false)
    }

    override fun setColor(colorValue: String) {
        val commandPrefix="C201F303C3" + colorValue+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setLightingMode() {
        val commandPrefix="C201F303C3" +"F100"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setCycleMode(cycleSpeed: Int) {
        val commandPrefix="C201F303C401F" + + (3 - cycleSpeed)
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setScene(sceneValue: Int) {
        val commandPrefix="C201F303C40" + (3 + sceneValue) + "F1"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }
}
