package com.ihomey.linkuphome.devicecontrol.controller.impl

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.devicecontrol.controller.ColorController
import com.ihomey.linkuphome.devicecontrol.controller.CommonController
import com.ihomey.linkuphome.devicecontrol.controller.SceneController
import com.ihomey.linkuphome.devicecontrol.controller.TimerController
import com.ihomey.linkuphome.getPeriodMinute


class N1Controller(val device: Device): CommonController, ColorController, SceneController, TimerController {

    override fun setBrightness(brightness: Int) {
        val commandPrefix="C201F103C2" + (if (brightness >= 1) Integer.toHexString(brightness+15) else "0" + Integer.toHexString(brightness+15))+ "00"
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

    override fun setTimer(minute: Int, hour: Int,isOpenTimer: Boolean, isOn: Boolean) {
        val commandPrefix="C201F104C5"+(if (isOn) "64" else "00") + String.format("%04x", getPeriodMinute(hour,minute))
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

}