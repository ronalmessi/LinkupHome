package com.ihomey.linkuphome.controller

import android.util.Log
import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.decodeHex

/**
 * Created by Administrator on 2017/6/16.
 */
class V2Controller : Controller() {

    companion object {
        val CODE_LIGHT_POWER_ON: String = "C201F303C164002816"
        val CODE_LIGHT_POWER_OFF: String = "C201F303C10000C416"
        val CODE_LIGHT_BRIGHT_BASE: String = "C201F303C2"
        val CODE_LIGHT_COLOR_BASE: String = "C201F303C3"
        val CODE_LIGHT_SPEED_BASE: String = "C201F303C401F"
        val CODE_LIGHT_SCENE_BASE: String = "C201F303C40"
        val CODE_LIGHT_TIMER_BASE: String = "C201F304"
    }

    override fun setLightColor(deviceId: Int, colorValue: String) {
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_BASE + colorValue
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_color_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16))
        val code_lawn_color = code_lawn_color_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_color.toCharArray()), false)
    }

    override fun setLightBright(deviceId: Int, brightValue: Int) {
        val code_lawn_bright_prefix = CODE_LIGHT_BRIGHT_BASE + if (brightValue>= 16) Integer.toHexString(brightValue) else "0" + Integer.toHexString(brightValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_bright_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(10, 12), 16))
        val code_lawn_bright = code_lawn_bright_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_bright.toCharArray()), false)
    }

    override fun setLightSpeed(deviceId: Int, speedValue: Int) {
        val code_lawn_speed_prefix = CODE_LIGHT_SPEED_BASE + (3 - speedValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_speed_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(12, 14), 16))
        val code_lawn_speed = code_lawn_speed_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId,decodeHex(code_lawn_speed.toCharArray()), false)
    }

    override fun setLightingMode(deviceId: Int) {
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_BASE + "F1"
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_color_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16))
        val code_lawn_color = code_lawn_color_prefix + "00" + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_color.toCharArray()), false)
    }

    override fun setLightPowerState( deviceId: Int, powerState: Int) {
        if (powerState == 1) {
            DataModelApi.sendData(deviceId, decodeHex(CODE_LIGHT_POWER_ON.toCharArray()), false)
        } else if (powerState == 0) {
            DataModelApi.sendData(deviceId, decodeHex(CODE_LIGHT_POWER_OFF.toCharArray()), false)
        }
    }

    override fun setLightScene(deviceId: Int, sceneValue: Int) {
        val code_lawn_scene_prefix = CODE_LIGHT_SCENE_BASE +(3+sceneValue)+"F1"
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_scene_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(12, 14), 16))
        val code_lawn_scene = code_lawn_scene_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_scene.toCharArray()), false)
    }

    fun setTimer(deviceId: Int, openMinuteValue: Int, openHourValue: Int,closeMinuteValue: Int,closeHourValue: Int) {
        val code_lawn_timer = "C201F305C7" + (if (openHourValue >= 10) ""+openHourValue else "0$openHourValue")+ (if (openMinuteValue >= 10) ""+openMinuteValue else "0$openMinuteValue")+(if (closeHourValue >= 10) ""+closeHourValue else "0$closeHourValue")+ (if (closeMinuteValue >= 10) ""+closeMinuteValue else "0$closeMinuteValue")
        Log.d("aa",code_lawn_timer)
        DataModelApi.sendData(deviceId, decodeHex(code_lawn_timer.toCharArray()), false)
    }
}