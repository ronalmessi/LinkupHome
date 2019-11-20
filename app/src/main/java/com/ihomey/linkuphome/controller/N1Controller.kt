package com.ihomey.linkuphome.controller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex


class N1Controller(val device: Device):CommonController,ColorController,SceneController {

    override fun setBrightness(brightness: Int) {
        val commandPrefix="C201F103C2" + if (brightness >= 16) Integer.toHexString(brightness) else "0" + Integer.toHexString(brightness)+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        DataModelApi.sendData(device.instructId, decodeHex((if (isOn) "C201F303C164002816" else "C201F303C10000C416").toCharArray()), false)
    }

    override fun setColor(colorValue: String) {
        val commandPrefix="C201F103C3" + colorValue+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setLightingMode() {
        val commandPrefix="C201F103C3" +"F100"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setCycleMode(cycleSpeed: Int) {
        val commandPrefix="C201F103C401F" + + (3 - cycleSpeed)
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setScene(sceneValue: Int) {
        if (sceneValue == 3) {
            setLightingMode()
        } else {
            var scene = sceneValue
            if (sceneValue == 0) {
                scene = 4
            } else if (sceneValue == 1) {
                scene = 5
            }
            val commandPrefix= "C201F103C402F$scene"
            val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
            DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
        }
    }
}
