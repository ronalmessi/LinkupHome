package com.ihomey.linkuphome.controller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.decodeHex

/**
 * Created by Administrator on 2017/6/16.
 */
class A2Controller : Controller() {

    companion object {
        val CODE_LIGHT_POWER_ON: String = "C201F203C164002816"
        val CODE_LIGHT_POWER_OFF: String = "C201F203C10000C416"
        val CODE_LIGHT_BRIGHT_BASE: String = "C201F203C2"
        val CODE_LIGHT_COLOR_TEMPERATURE_BASE: String = "C201F203C3"
        val CODE_LIGHT_TIMER_BASE: String = "C201F204C4"
    }

    override fun setLightColorTemperature(deviceId: Int, colorTemperatureValue: Int) {
        val colorTemperatureValue = ((colorTemperatureValue - 3000) * 100 / (50 * 70))
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_TEMPERATURE_BASE + if (colorTemperatureValue >= 16) Integer.toHexString(colorTemperatureValue) else "0" + Integer.toHexString(colorTemperatureValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_color_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16))
        val code_lawn_color = code_lawn_color_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_color.toUpperCase().toCharArray()), false)
    }

    override fun setLightBright(deviceId: Int, brightValue: Int) {
        val code_lawn_bright_prefix = CODE_LIGHT_BRIGHT_BASE + if (brightValue >= 16) Integer.toHexString(brightValue) else "0" + Integer.toHexString(brightValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_bright_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(10, 12), 16))
        val code_lawn_bright = code_lawn_bright_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_bright.toUpperCase().toCharArray()), false)
    }

    override fun setLightPowerState(deviceId: Int, powerState: Int) {
        if (powerState == 1) {
            DataModelApi.sendData(deviceId, decodeHex(CODE_LIGHT_POWER_ON.toCharArray()), false)
        } else if (powerState == 0) {
            DataModelApi.sendData(deviceId, decodeHex(CODE_LIGHT_POWER_OFF.toCharArray()), false)
        }
    }

    override fun setTimer(deviceId: Int, minuteValue: Int, isOn: Boolean) {
        val code_lawn_timer_prefix = CODE_LIGHT_TIMER_BASE + (if (isOn) "64" else "00") + String.format("%04x", minuteValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_timer.toUpperCase().toCharArray()), false)
    }

}