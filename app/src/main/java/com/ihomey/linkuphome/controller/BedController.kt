package com.ihomey.linkuphome.controller

import android.text.TextUtils
import android.util.Log
import com.clj.fastble.BleManager
import com.ihomey.linkuphome.data.vo.Alarm
import com.ihomey.linkuphome.write


/**
 * Created by Administrator on 2017/6/16.
 */
class BedController : Controller() {

    companion object {
        val UUID_SERVICE = "00001c00-d102-11e1-9b23-000efb0000c6"
        val UUID_CHARACTERISTIC_WRITE = "00001c01-d102-11e1-9b23-000efb0000c6"
        val UUID_CHARACTERISTIC_READ = "00001c0f-d102-11e1-9b23-000efb0000c6"


        val CODE_LIGHT_POWER_ON: String = "BF01D101CD03C201642A16"
        val CODE_LIGHT_POWER_OFF: String = "BF01D101CD03C20100C616"
        val CODE_LIGHT_BRIGHT_BASE: String = "BF01D101CD03C202"
        val CODE_LIGHT_COLOR_BASE: String = "BF01D101CD04C203F1"
        val CODE_LIGHT_SPEED_BASE: String = "BF01D101CD03C204"
        val CODE_LIGHT_COLOR_TEMPERATURE_BASE: String = "BF01D101CD04C203F2"
        val CODE_LIGHT_SYNC_TIME_BASE: String = "BF01D101CD09C301"
        val CODE_LIGHT_TIMER_BASE: String = "BF01D101CD08C20601"
        val CODE_LIGHT_TIMER_DISABLE_BASE: String = "BF01D101CD04C20602"
        val CODE_LIGHT_ALARM_BASE: String = "BF01D101CD07C401"
        val CODE_LIGHT_ALARM_DISABLE_BASE: String = "BF01D101CD03C402"
        val CODE_LIGHT_ALARM_TYPE_BASE: String = "BF01D101CD05C40401"


        val CODE_LIGHT_NOTIFY_VERSION: String = "BF01D101CD04C101F101"
        val CODE_LIGHT_NOTIFY_TEMPERATURE_HUMIDITY_F2: String = "BF01D101CD04C10204EF"
        val CODE_LIGHT_NOTIFY_TEMPERATURE_HUMIDITY_F1: String = "BF01D101CD04C10104EF"
        val CODE_LIGHT_NOTIFY_FORMALDEHYDE: String = "BF01D101CD04C10202EF"
        val CODE_LIGHT_NOTIFY_VOC: String = "BF01D101CD04C10203EF"
        val CODE_LIGHT_NOTIFY_PM25: String = "BF01D101CD04C10201EF"


    }

    override fun setLightBright(mac: String, brightValue: Int) {
        val code_lawn_bright_prefix = CODE_LIGHT_BRIGHT_BASE + if (brightValue >= 16) Integer.toHexString(brightValue) else "0" + Integer.toHexString(brightValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_bright_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(16, 18), 16))
        val code_lawn_bright = code_lawn_bright_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
//        BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_bright)


        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_bright)
    }

    override fun setLightPowerState(mac: String, powerState: Int) {
        if (powerState == 1) {
//            BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, CODE_LIGHT_POWER_ON)

            BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, CODE_LIGHT_POWER_ON)
        } else if (powerState == 0) {
//            BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, CODE_LIGHT_POWER_OFF)

            BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, CODE_LIGHT_POWER_OFF)
        }
    }

    override fun setLightColor(mac: String, colorValue: String) {
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_BASE + colorValue
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_color_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_color_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_color_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_color_prefix.substring(18, 20), 16))
        val code_lawn_color = code_lawn_color_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_color)
    }

    override fun setLightSpeed(mac: String, speedValue: Int) {
        var speed = "02"
        when (speedValue) {
            2 -> speed = "02"
            1 -> speed = "05"
            0 -> speed = "09"
        }
        val code_lawn_speed_prefix = CODE_LIGHT_SPEED_BASE + speed
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_speed_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(16, 18), 16))
        val code_lawn_speed = code_lawn_speed_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
//        BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_speed)


        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_speed)
    }

    override fun setLightScene(mac: String, sceneValue: Int) {
        var speed = 29 + sceneValue
        val code_lawn_scene_prefix = CODE_LIGHT_COLOR_BASE + Integer.toHexString(speed).toUpperCase()
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_scene_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(18, 20), 16))
        val code_lawn_scene = code_lawn_scene_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
//        BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_scene)
        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_scene)
    }

    override fun setLightColorTemperature(mac: String, colorTemperatureValue: Int) {
        var colorTemperature = "F1"
        when (colorTemperatureValue) {
            3000 -> colorTemperature = "F1"
            4000 -> colorTemperature = "F2"
            6500 -> colorTemperature = "F3"
        }
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_TEMPERATURE_BASE + colorTemperature
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_color_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_color_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_color_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_color_prefix.substring(18, 20), 16))
        val code_lawn_color = code_lawn_color_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
//        BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_color)

        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_color)
    }


    override fun setRepeatTimer(mac: String, minuteValue: Int, hourValue: Int, isOpenTimer: Boolean, isOn: Boolean, dayOfWeek: Int) {
        val timerId = if (isOpenTimer) "02" else "01"
        if (isOn) {
            val isRepeat = if (dayOfWeek > 0) Integer.toHexString(dayOfWeek + 128) else "00"
            val code_lawn_timer_prefix = CODE_LIGHT_TIMER_BASE + timerId + isRepeat + (if (hourValue >= 10) "" + hourValue else "0$hourValue") + (if (minuteValue >= 10) "" + minuteValue else "0$minuteValue") + if (isOpenTimer) "64" else "00"
            val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(20, 22), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(22, 24), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(24, 26), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(26, 28), 16))
            val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
//            BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)

            BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)
        } else {
            cancleTimer(mac, timerId)
        }
    }

    fun cancleTimer(mac: String, timerId: String) {
        val code_lawn_timer_prefix = CODE_LIGHT_TIMER_DISABLE_BASE + timerId
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
//        BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)

        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)
    }

    fun cancelAlarm(mac: String, alarmId: Int) {
        val code_lawn_timer_prefix = CODE_LIGHT_ALARM_DISABLE_BASE + "0" + alarmId
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        Log.d("aa", code_lawn_timer)
//        BluetoothClientManager.getInstance().client.write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)

        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)
    }

    fun setAlarm(mac: String, alarm: Alarm) {
        val alarmId = "0" + alarm.id
        val hour = alarm.hour
        val minute = alarm.minute
        val isRepeat = if (alarm.dayOfWeek > 0) Integer.toHexString(alarm.dayOfWeek + 128) else "00"
        val code_lawn_timer_prefix = CODE_LIGHT_ALARM_BASE + alarmId + isRepeat + (if (hour >= 10) "" + hour else "0$hour") + (if (minute >= 10) "" + minute else "0$minute") + "0" + alarm.ringType
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(20, 22), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(22, 24), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(24, 26), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        Log.d("aa", code_lawn_timer)
        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)
    }


    fun setAlarmType(mac: String, alarm: Alarm) {
        val alarmId = "0" + alarm.id
        val type = "0" + alarm.type
        val code_lawn_timer_prefix = CODE_LIGHT_ALARM_TYPE_BASE + alarmId + type
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        Log.d("aa", code_lawn_timer)
        BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_timer)
    }

    fun getSensorVersion(mac: String) {
        val code_check = Integer.toHexString(Integer.parseInt(CODE_LIGHT_NOTIFY_VERSION.substring(10, 12), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VERSION.substring(12, 14), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VERSION.substring(14, 16), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VERSION.substring(16, 18), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VERSION.substring(18, 20), 16))
        val code_lawn_time = CODE_LIGHT_NOTIFY_VERSION + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BleManager.getInstance().write(mac, BedController.UUID_SERVICE, BedController.UUID_CHARACTERISTIC_WRITE, code_lawn_time)
    }


    fun getTemperatureAndHumidity(mac: String, sensorType: String) {
        val code_lawn_timer_prefix = if (TextUtils.equals("F1", sensorType)) CODE_LIGHT_NOTIFY_TEMPERATURE_HUMIDITY_F1 else CODE_LIGHT_NOTIFY_TEMPERATURE_HUMIDITY_F2
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16))
        val code_lawn_time = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BleManager.getInstance().write(mac, BedController.UUID_SERVICE, BedController.UUID_CHARACTERISTIC_WRITE, code_lawn_time)
    }

    fun getFormaldehyde(mac: String) {
        val code_check = Integer.toHexString(Integer.parseInt(CODE_LIGHT_NOTIFY_FORMALDEHYDE.substring(10, 12), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_FORMALDEHYDE.substring(12, 14), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_FORMALDEHYDE.substring(14, 16), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_FORMALDEHYDE.substring(16, 18), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_FORMALDEHYDE.substring(18, 20), 16))
        val code_lawn_time = CODE_LIGHT_NOTIFY_FORMALDEHYDE + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BleManager.getInstance().write(mac, BedController.UUID_SERVICE, BedController.UUID_CHARACTERISTIC_WRITE, code_lawn_time)
    }

    fun getVOCValue(mac: String) {
        val code_check = Integer.toHexString(Integer.parseInt(CODE_LIGHT_NOTIFY_VOC.substring(10, 12), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VOC.substring(12, 14), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VOC.substring(14, 16), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VOC.substring(16, 18), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_VOC.substring(18, 20), 16))
        val code_lawn_time = CODE_LIGHT_NOTIFY_VOC + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BleManager.getInstance().write(mac, BedController.UUID_SERVICE, BedController.UUID_CHARACTERISTIC_WRITE, code_lawn_time)
    }

    fun getPM25Value(mac: String) {
        val code_check = Integer.toHexString(Integer.parseInt(CODE_LIGHT_NOTIFY_PM25.substring(10, 12), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_PM25.substring(12, 14), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_PM25.substring(14, 16), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_PM25.substring(16, 18), 16) + Integer.parseInt(CODE_LIGHT_NOTIFY_PM25.substring(18, 20), 16))
        val code_lawn_time = CODE_LIGHT_NOTIFY_PM25 + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BleManager.getInstance().write(mac, BedController.UUID_SERVICE, BedController.UUID_CHARACTERISTIC_WRITE, code_lawn_time)
    }
}