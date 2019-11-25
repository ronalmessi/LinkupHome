package com.ihomey.linkuphome.devicecontrol.controller.impl

import com.ihomey.linkuphome.checkSum
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.devicecontrol.controller.ColorController
import com.ihomey.linkuphome.devicecontrol.controller.ColorTemperatureController
import com.ihomey.linkuphome.devicecontrol.controller.CommonController
import com.ihomey.linkuphome.devicecontrol.controller.SceneController
import com.ihomey.linkuphome.spp.BluetoothSPP
import java.util.*


class M1LightController(val device: Device) : CommonController, ColorController, ColorTemperatureController, SceneController {

    override fun setBrightness(brightness: Int) {
        val commandPrefix = "BF01D101CD03C202" + (if (brightness >= 1) Integer.toHexString(brightness + 15) else "0" + Integer.toHexString(brightness + 15))
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    override fun setOnOff(isOn: Boolean) {
        BluetoothSPP.getInstance().send(device.id, decodeHex((if (isOn) "BF01D101CD03C201642A16" else "BF01D101CD03C20100C616").toCharArray()), false)
    }


    override fun setColor(colorValue: String) {
        val commandPrefix = "BF01D101CD04C203F1$colorValue"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    override fun setLightingMode() {
        val commandPrefix = "BF01D101CD04C203F2" + "F2"
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
        val commandPrefix = "BF01D101CD03C204$speed"
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
        val commandPrefix = "BF01D101CD04C203F2$colorTemperatureValue"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    override fun setScene(sceneValue: Int) {
        val commandPrefix = "BF01D101CD04C203F1" + Integer.toHexString(29 + sceneValue).toUpperCase(Locale.getDefault())
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }


    fun setSleepMode(state: Int) {
        BluetoothSPP.getInstance().send(device.id, decodeHex((if (state == 0) "BF01D101CD04C2090102D216" else "BF01D101CD04C2090101D116").toCharArray()), false)
    }

    fun enableGestureControl(isEnable: Boolean) {
        BluetoothSPP.getInstance().send(device.id, decodeHex((if (isEnable) "BF01D101CD04C2070101CF16" else "BF01D101CD04C2070102D016").toCharArray()), false)
    }

    fun setAlarmType(alarm: Alarm) {
        val alarmId = "0" + alarm.id
        val type = "0" + alarm.type
        val commandPrefix = "BF01D101CD05C40401$alarmId$type"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    fun cancelAlarm(alarmId: Int) {
        val commandPrefix = "BF01D101CD03C4020$alarmId"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    fun stopAlarmRing() {
        val commandPrefix = "BF01D101CD04C4030101"
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    fun setAlarm(alarm: Alarm) {
        val alarmId = "0" + alarm.id
        val hour = alarm.hour
        val minute = alarm.minute
        val isRepeat = if (alarm.dayOfWeek > 0) Integer.toHexString(alarm.dayOfWeek + 128) else "00"
        val commandPrefix = "BF01D101CD07C401" + alarmId + isRepeat + (if (hour >= 10) "" + hour else "0$hour") + (if (minute >= 10) "" + minute else "0$minute") + "0" + alarm.ringType
        val command = commandPrefix + checkSum(commandPrefix.substring(10)) + "16"
        BluetoothSPP.getInstance().send(device.id, decodeHex(command.toCharArray()), false)
    }

    fun getEnvironmentalValue() {
        BluetoothSPP.getInstance().send(device.id, decodeHex(("BF01D101CD04C10207EFBD16").toCharArray()), false)
    }

    fun getFirmwareVersion() {
        BluetoothSPP.getInstance().send(device.id, decodeHex(("BF01D101CD04C102F101B916").toCharArray()), false)
    }
}
