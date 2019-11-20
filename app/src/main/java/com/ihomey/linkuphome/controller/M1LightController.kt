package com.ihomey.linkuphome.controller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.spp.BluetoothSPP
import java.util.*


class M1LightController(val device: Device):CommonController,ColorController ,ColorTemperatureController,SceneController{

    override fun setBrightness(brightness: Int) {
        val commandPrefix="BF01D101CD03C202" + if (brightness >= 16) Integer.toHexString(brightness) else "0" + Integer.toHexString(brightness)
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        BluetoothSPP.getInstance().send(device.id, decodeHex((if (isOn) "BF01D101CD03C201642A16" else "BF01D101CD03C20100C616").toCharArray()), false)
    }


    override fun setColor(colorValue: String) {
        val commandPrefix= "BF01D101CD04C203F1$colorValue"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    override fun setLightingMode() {
        val commandPrefix="BF01D101CD04C203F2" +"F2"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    override fun setCycleMode(cycleSpeed: Int) {
        var speed = "02"
        when (cycleSpeed) {
            2 -> speed = "02"
            1 -> speed = "05"
            0 -> speed = "09"
        }
        val commandPrefix= "BF01D101CD03C204$speed"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    override fun setColorTemperature(colorTemperature: Int) {
        var colorTemperatureValue = "F1"
        when (colorTemperature) {
            3000 -> colorTemperatureValue = "F1"
            4000 -> colorTemperatureValue = "F2"
            6500 -> colorTemperatureValue = "F3"
        }
        val commandPrefix= "BF01D101CD04C203F2$colorTemperatureValue"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

    override fun setScene(sceneValue: Int) {
        val commandPrefix="BF01D101CD04C203F1" +Integer.toHexString(29 + sceneValue).toUpperCase(Locale.getDefault())
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }
}
