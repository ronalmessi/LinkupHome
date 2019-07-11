package com.ihomey.linkuphome.controller

import android.util.Log
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.spp.BluetoothSPP
import java.util.*

/**
 * Created by Administrator on 2017/6/16.
 */
class M1Controller : Controller() {

    companion object {

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


        val CODE_LIGHT_ALARM_TYPE_BASE: String = "BF01D101CD04C208"


        val CODE_LIGHT_SLEEP_MODE_ON: String = "BF01D101CD04C2090101D116"
        val CODE_LIGHT_SLEEP_MODE_OFF: String = "BF01D101CD04C2090102D216"

        val CODE_LIGHT_GESTURE_CONTROL_MODE_ON: String = "BF01D101CD04C2070101CF16"
        val CODE_LIGHT_GESTURE_CONTROL_MODE_OFF: String = "BF01D101CD04C2070102D016"


        val CODE_LIGHT_NOTIFY_VERSION: String = "BF01D101CD04C102F101"

        val CODE_LIGHT_NOTIFY_ALL: String = "BF01D101CD04C10207EF"

    }

    override fun setLightBright(deviceId: Int, brightValue: Int) {
        val code_lawn_bright_prefix = CODE_LIGHT_BRIGHT_BASE + if (brightValue >= 16) Integer.toHexString(brightValue) else "0" + Integer.toHexString(brightValue)
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_bright_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_bright_prefix.substring(16, 18), 16))
        val code_lawn_bright = code_lawn_bright_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_bright.toUpperCase().toCharArray()),false)
    }

    override fun setLightPowerState(deviceId: Int, powerState: Int) {
        if (powerState == 1) {
            BluetoothSPP.getInstance().send(decodeHex(CODE_LIGHT_POWER_ON.toUpperCase().toCharArray()),false)
        } else if (powerState == 0) {
            BluetoothSPP.getInstance().send(decodeHex(CODE_LIGHT_POWER_OFF.toUpperCase().toCharArray()),false)
        }
    }

    override fun setLightingMode(deviceId: Int) {
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_TEMPERATURE_BASE + "F2"
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_color_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_color_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_color_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_color_prefix.substring(18, 20), 16))
        val code_lawn_color = code_lawn_color_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_color.toUpperCase().toCharArray()),false)
    }

    override fun setSleepMode(state: Int) {
        if (state == 1) {
            BluetoothSPP.getInstance().send(decodeHex(CODE_LIGHT_SLEEP_MODE_ON.toUpperCase().toCharArray()),false)
        } else if (state == 0) {
            BluetoothSPP.getInstance().send(decodeHex(CODE_LIGHT_SLEEP_MODE_OFF.toUpperCase().toCharArray()),false)
        }
    }

    override fun enableGestureControl(isEnable: Boolean) {
        super.enableGestureControl(isEnable)
        if (isEnable) {
            BluetoothSPP.getInstance().send(decodeHex(CODE_LIGHT_GESTURE_CONTROL_MODE_ON.toUpperCase().toCharArray()),false)
        } else {
            BluetoothSPP.getInstance().send(decodeHex(CODE_LIGHT_GESTURE_CONTROL_MODE_OFF.toUpperCase().toCharArray()),false)
        }
    }

    override fun syncTime() {
        super.syncTime()
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val year = calendar.get(Calendar.YEAR)%2000
        val month = calendar.get(Calendar.MONTH)+1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val code_lawn_time_prefix =CODE_LIGHT_SYNC_TIME_BASE+(if (year>= 10) "" + year else "0$year")+(if (month>= 10) "" + month else "0$month")+(if (dayOfMonth>= 10) "" + dayOfMonth else "0$dayOfMonth")+(if (dayOfWeek>= 10) "" + dayOfWeek else "0$dayOfWeek") + (if (hour >= 10) "" + hour else "0$hour") + (if (minute >= 10) "" + minute else "0$minute") + (if (second >= 10) "" + second else "0$second")
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_time_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_time_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_time_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_time_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_time_prefix.substring(18, 20), 16)+ Integer.parseInt(code_lawn_time_prefix.substring(20, 22), 16)+ Integer.parseInt(code_lawn_time_prefix.substring(22, 24), 16)+ Integer.parseInt(code_lawn_time_prefix.substring(24, 26), 16)+ Integer.parseInt(code_lawn_time_prefix.substring(26, 28), 16)+ Integer.parseInt(code_lawn_time_prefix.substring(28, 30), 16))
        val code_lawn_time = code_lawn_time_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_time.toUpperCase().toCharArray()),false)
    }


    override fun setLightColor(deviceId: Int, colorValue: String) {
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_BASE + colorValue
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_color_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_color_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_color_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_color_prefix.substring(18, 20), 16))
        val code_lawn_color = code_lawn_color_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_color.toUpperCase().toCharArray()),false)
    }

    override fun setLightSpeed(deviceId: Int, speedValue: Int) {
        var speed = "02"
        when (speedValue) {
            2 -> speed = "02"
            1 -> speed = "05"
            0 -> speed = "09"
        }
        val code_lawn_speed_prefix = CODE_LIGHT_SPEED_BASE + speed
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_speed_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_speed_prefix.substring(16, 18), 16))
        val code_lawn_speed = code_lawn_speed_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_speed.toUpperCase().toCharArray()),false)
    }

    override fun setLightScene(deviceId: Int, sceneValue: Int) {
        val speed = 29 + sceneValue
        val code_lawn_scene_prefix = CODE_LIGHT_COLOR_BASE + Integer.toHexString(speed).toUpperCase()
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_scene_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_scene_prefix.substring(18, 20), 16))
        val code_lawn_scene = code_lawn_scene_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_scene.toUpperCase().toCharArray()),false)
    }

    override fun setLightColorTemperature(deviceId: Int, colorTemperatureValue: Int) {
        var colorTemperature = "F1"
        when (colorTemperatureValue) {
            3000 -> colorTemperature = "F1"
            4000 -> colorTemperature = "F2"
            6500 -> colorTemperature = "F3"
        }
        val code_lawn_color_prefix = CODE_LIGHT_COLOR_TEMPERATURE_BASE + colorTemperature
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_color_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_color_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_color_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_color_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_color_prefix.substring(18, 20), 16))
        val code_lawn_color = code_lawn_color_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_color.toUpperCase().toCharArray()),false)
    }


    override fun setRepeatTimer(minuteValue: Int, hourValue: Int, isOpenTimer: Boolean, isOn: Boolean, dayOfWeek: Int) {
        val timerId = if (isOpenTimer) "02" else "01"
        if (isOn) {
            val isRepeat = if (dayOfWeek > 0) Integer.toHexString(dayOfWeek + 128) else "00"
            val code_lawn_timer_prefix = CODE_LIGHT_TIMER_BASE + timerId + isRepeat + (if (hourValue >= 10) "" + hourValue else "0$hourValue") + (if (minuteValue >= 10) "" + minuteValue else "0$minuteValue") + if (isOpenTimer) "64" else "00"
            val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(20, 22), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(22, 24), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(24, 26), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(26, 28), 16))
            val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
            BluetoothSPP.getInstance().send(decodeHex(code_lawn_timer.toUpperCase().toCharArray()),false)
        } else {
            cancleTimer(timerId)
        }
    }

    private fun cancleTimer(timerId: String) {
        val code_lawn_timer_prefix = CODE_LIGHT_TIMER_DISABLE_BASE + timerId
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_timer.toUpperCase().toCharArray()),false)

    }

    fun setAlarmType(alarm: Alarm) {
        val alarmId = "0" + alarm.id
        val type = "0" + alarm.type
        val code_lawn_timer_prefix = CODE_LIGHT_ALARM_TYPE_BASE + alarmId + type
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_timer.toUpperCase().toCharArray()),false)
    }

    fun cancelAlarm(alarmId: Int) {
        val code_lawn_timer_prefix = CODE_LIGHT_ALARM_DISABLE_BASE + "0" + alarmId
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_timer.toUpperCase().toCharArray()),false)
    }

    fun setAlarm(alarm: Alarm) {
        val alarmId = "0" + alarm.id
        val hour = alarm.hour
        val minute = alarm.minute
        val isRepeat = if (alarm.dayOfWeek > 0) Integer.toHexString(alarm.dayOfWeek + 128) else "00"
        val code_lawn_timer_prefix = CODE_LIGHT_ALARM_BASE + alarmId + isRepeat + (if (hour >= 10) "" + hour else "0$hour") + (if (minute >= 10) "" + minute else "0$minute") + "0" + alarm.ringType
        val code_check = Integer.toHexString(Integer.parseInt(code_lawn_timer_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(18, 20), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(20, 22), 16) + Integer.parseInt(code_lawn_timer_prefix.substring(22, 24), 16)+ Integer.parseInt(code_lawn_timer_prefix.substring(24, 26), 16))
        val code_lawn_timer = code_lawn_timer_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
        BluetoothSPP.getInstance().send(decodeHex(code_lawn_timer.toUpperCase().toCharArray()),false)
    }
}