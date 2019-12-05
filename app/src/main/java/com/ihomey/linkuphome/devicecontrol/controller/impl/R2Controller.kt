package com.ihomey.linkuphome.devicecontrol.controller.impl

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.devicecontrol.controller.*
import com.ihomey.linkuphome.getPeriodMinute
import com.ihomey.linkuphome.csrmesh.CSRMeshServiceManager


class R2Controller(val device: Device): CommonController, ColorController, ColorTemperatureController, SceneController, TimerController {


    override fun setBrightness(brightness: Int) {
        val commandPrefix="C201F103C2" + (if (brightness >= 1) Integer.toHexString(brightness+15) else "0" + Integer.toHexString(brightness+15))+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex((if (isOn) "C201F103C164002816" else "C201F103C10000C416").toCharArray()), false)
    }

    override fun setColor(colorValue: String) {
        val commandPrefix="C201F103C3" + colorValue+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setLightingMode() {
        val commandPrefix="C201F103C3" +"F100"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setCycleMode(cycleSpeed: Int) {
        val commandPrefix="C201F103C401F" + (3 - cycleSpeed)
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setColorTemperature(colorTemperature: Int) {
        var colorTemperatureValue = "F1"
        when (colorTemperature) {
            3000 -> colorTemperatureValue = "F1"
            4000 -> colorTemperatureValue = "F2"
            6500 -> colorTemperatureValue = "F3"
        }
        val commandPrefix="C201F103C3" + colorTemperatureValue + "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setScene(sceneValue: Int) {
        val commandPrefix="C201F103C402F" + (sceneValue + 1)
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setTimer(minute: Int, hour: Int,isOpenTimer: Boolean, isOn: Boolean) {
        val commandPrefix="C201F104C5"+(if (isOn) "64" else "00") + String.format("%04x", getPeriodMinute(hour,minute))
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }
}
