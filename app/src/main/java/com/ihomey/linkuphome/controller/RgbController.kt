package com.ihomey.linkuphome.controller

import android.util.Log
import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.decodeHex

/**
 * Created by Administrator on 2017/6/16.
 */
class RgbController : Controller() {

    companion object {
        val CODE_LIGHT_POWER_ON: String = "C201F103C164002816"
        val CODE_LIGHT_POWER_OFF: String = "C201F103C10000C416"
        val CODE_LIGHT_BRIGHT_BASE: String = "C201F103C2"
        val CODE_LIGHT_COLOR_BASE: String = "C201F103C3"
        val CODE_LIGHT_SPEED_BASE: String = "C201F103C401F"
        val CODE_LIGHT_SCENE_BASE: String = "C201F103C402F"
        val CODE_LIGHT_TIMER_BASE: String = "C201F104C5"
    }

    override fun setLightColor(deviceId: Int, colorValue: String) {
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_BASE + colorValue
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_color_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16))
        val code_lawn_color = code_lawn_color_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_color.toCharArray()), false)
    }

    override fun setLightBright(deviceId: Int, brightValue: Int) {
        val code_lawn_bright_prefix = CODE_LIGHT_BRIGHT_BASE + if (brightValue >= 16) Integer.toHexString(brightValue) else "0" + Integer.toHexString(brightValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_bright_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(10, 12), 16))
        val code_lawn_bright = code_lawn_bright_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_bright.toCharArray()), false)
    }

    override fun setLightSpeed(deviceId: Int, speedValue: Int) {
        val code_lawn_speed_prefix = CODE_LIGHT_SPEED_BASE + (3 - speedValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_speed_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(12, 14), 16))
        val code_lawn_speed = code_lawn_speed_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_speed.toCharArray()), false)
    }

    override fun setLightingMode(deviceId: Int) {
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_BASE + "F1"
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_color_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16))
        val code_lawn_color = code_lawn_color_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_color.toCharArray()), false)
    }

    override fun setLightPowerState(deviceId: Int, powerState: Int) {
        if (powerState == 1) {
            DataModelApi.sendData(deviceId, decodeHex(CODE_LIGHT_POWER_ON.toCharArray()), false)
        } else if (powerState == 0) {
            DataModelApi.sendData(deviceId, decodeHex(CODE_LIGHT_POWER_OFF.toCharArray()), false)
        }
    }

    override fun setLightScene(deviceId: Int, sceneValue: Int) {
        val code_lawn_scene_prefix = CODE_LIGHT_SCENE_BASE + (sceneValue + 1)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_scene_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(12, 14), 16))
        val code_lawn_scene = code_lawn_scene_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_scene.toCharArray()), false)
    }

    override fun setTimer(deviceId: Int, minuteValue: Int, isOn: Boolean) {
        val code_lawn_timer_prefix = CODE_LIGHT_TIMER_BASE + (if (isOn) "64" else "00") + String.format("%04x", minuteValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_timer.toCharArray()), false)
    }

    override fun setLightColorTemperature(deviceId: Int, colorTemperatureValue: Int) {
        var colorTemperature = "F1"
        when (colorTemperatureValue) {
            3000 -> colorTemperature = "F1"
            4000 -> colorTemperature = "F2"
            6500 -> colorTemperature = "F3"
        }
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_BASE + colorTemperature
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_color_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16))
        val code_lawn_color = code_lawn_color_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"

        Log.d("aa","---"+code_lawn_color)
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_color.toCharArray()), false)
    }
}