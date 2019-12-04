package com.ihomey.linkuphome.devicecontrol.controller.impl

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.devicecontrol.controller.ColorTemperatureController
import com.ihomey.linkuphome.devicecontrol.controller.CommonController
import com.ihomey.linkuphome.devicecontrol.controller.TimerController
import com.ihomey.linkuphome.getPeriodMinute
import com.ihomey.linkuphome.sigmesh.CSRMeshServiceManager


class A2Controller(val device: Device): CommonController, ColorTemperatureController, TimerController {

    override fun setBrightness(brightness: Int) {
        val commandPrefix="C201F203C2" + (if (brightness >= 1) Integer.toHexString(brightness+15) else "0" + Integer.toHexString(brightness+15))+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex((if (isOn) "C201F203C164002816" else "C201F203C10000C416").toCharArray()), false)
    }

    override fun setColorTemperature(colorTemperature: Int) {
        val colorTemperatureValue = ((colorTemperature - 3000) * 100 / (50 * 70))
        val commandPrefix="C201F203C3" + (if (colorTemperatureValue >= 16) Integer.toHexString(colorTemperatureValue) else "0" + Integer.toHexString(colorTemperatureValue))+ "00"
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setTimer(minute: Int, hour: Int,isOpenTimer: Boolean, isOn: Boolean) {
        val commandPrefix="C201F204C4"+(if (isOn) "64" else "00") + String.format("%04x", getPeriodMinute(hour,minute))
        val command = commandPrefix + checkSum(commandPrefix.substring(6)) + "16"
        if(CSRMeshServiceManager.getInstance().isConnected()) DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

}
