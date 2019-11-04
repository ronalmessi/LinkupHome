package com.ihomey.linkuphome.controller

import android.util.Log
import com.ihomey.linkuphome.AppConfig
import com.pairlink.sigmesh.lib.PlSigMeshService
import com.pairlink.sigmesh.lib.Util
import java.util.*


/**
 * Created by Administrator on 2017/6/16.
 */
class SigMeshController : Controller() {

    companion object {
        val CODE_LIGHT_TIMER_BASE: String = "7FB502"
    }

    override fun setLightColor(deviceId: Int, colorValue: String) {
        val rgb = AppConfig.RGB_COLOR[Integer.parseInt(colorValue, 16)]
        setLightRGB(deviceId.toShort(), (rgb.r * 255).toFloat(), (rgb.g * 255).toFloat(), (rgb.b * 255).toFloat())
    }

    override fun setLightBright(deviceId: Int, brightValue: Int) {
        PlSigMeshService.getInstance().setLightCTL(deviceId.toShort(), (brightValue * 2000).toShort(), Util.PL_LIGHT_CTL_TEMPERATURE_MIN, 0.toShort(), Util.PL_DEFAULT_ONOFF_TRANSITIONTIME, Util.PL_DEFAULT_ONOFF_DELAY, Util.PL_DEFAULT_APP_KEY_INDEX, false)
    }

    override fun setLightSpeed(deviceId: Int, speedValue: Int) {
        PlSigMeshService.getInstance().vendorUartSend(deviceId.toShort(), Util.hexStringToBytes("7FB4010" + (3 - speedValue)), Util.PL_DEFAULT_APP_KEY_INDEX)
    }

    override fun setLightingMode(deviceId: Int) {
        PlSigMeshService.getInstance().vendorUartSend(deviceId.toShort(), Util.hexStringToBytes("7FB3010FA0"), Util.PL_DEFAULT_APP_KEY_INDEX)
    }

    override fun setLightPowerState(deviceId: Int, powerState: Int) {
        if (powerState == 1) {
            PlSigMeshService.getInstance().setOnoff(deviceId.toShort(), Util.PL_SIG_ONOFF_SET_ON, Util.PL_DEFAULT_ONOFF_TRANSITIONTIME, Util.PL_DEFAULT_ONOFF_DELAY, Util.PL_DEFAULT_APP_KEY_INDEX, true)
        } else if (powerState == 0) {
            PlSigMeshService.getInstance().setOnoff(deviceId.toShort(), Util.PL_SIG_ONOFF_SET_OFF, Util.PL_DEFAULT_ONOFF_TRANSITIONTIME, Util.PL_DEFAULT_ONOFF_DELAY, Util.PL_DEFAULT_APP_KEY_INDEX, true)
        }
    }

    override fun setLightScene(deviceId: Int, sceneValue: Int) {
        PlSigMeshService.getInstance().vendorUartSend(deviceId.toShort(), Util.hexStringToBytes("7FB403F" + (sceneValue + 1)), Util.PL_DEFAULT_APP_KEY_INDEX)
    }


    override fun syncTime(deviceId: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val year = calendar.get(Calendar.YEAR) % 2000
        val month = calendar.get(Calendar.MONTH) + 1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val code_lawn_date = "7FB5F101" + (if (year >= 10) "" + year else "0$year") + month + dayOfWeek + (if (dayOfMonth >= 10) "" + dayOfMonth else "0$dayOfMonth")
        Log.d("aa", code_lawn_date)
        PlSigMeshService.getInstance().vendorUartSend(deviceId.toShort(), Util.hexStringToBytes(code_lawn_date), Util.PL_DEFAULT_APP_KEY_INDEX)

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val code_lawn_time = "7FB5F102" + (if (hour >= 10) "" + hour else "0$hour") + (if (minute >= 10) "" + minute else "0$minute") + (if (second >= 10) "" + second else "0$second")
        Log.d("aa", code_lawn_time)
        PlSigMeshService.getInstance().vendorUartSend(deviceId.toShort(), Util.hexStringToBytes(code_lawn_time), Util.PL_DEFAULT_APP_KEY_INDEX)
    }

    override fun setRepeatTimer(deviceId: Int, minuteValue: Int, hourValue: Int, isOpenTimer: Boolean, isOn: Boolean, isRepeat: Boolean) {
        val code_lawn_timer = CODE_LIGHT_TIMER_BASE + (if (isOpenTimer) "01" else "02") + (if (isOn) (if (isRepeat) "FF" else "80") else "00") + (if (hourValue >= 10) "" + hourValue else "0$hourValue") + (if (minuteValue >= 10) "" + minuteValue else "0$minuteValue")
        Log.d("aa", code_lawn_timer)
        PlSigMeshService.getInstance().vendorUartSend(deviceId.toShort(), Util.hexStringToBytes(code_lawn_timer), Util.PL_DEFAULT_APP_KEY_INDEX)
    }

    private fun setLightRGB(dst: Short, r1: Float, g1: Float, b1: Float) {
        var h = 0.0
        var s = 0.0
        var l = 0.0

        val r = r1 / 65535
        val g = g1 / 65535
        val b = b1 / 65535

        var v = Math.max(r, g)
        v = Math.max(v, b)

        var m = Math.min(r, g)
        m = Math.min(m, b)

        l = (m + v) / 2.0
        if (l <= 0)
            return

        val vm = v - m

        s = vm.toDouble()

        if (s > 0.0)
            s /= if (l <= 0.5) v.toDouble() + m.toDouble() else 2.0 - v.toDouble() - m.toDouble()
        else
            return

        val r2 = (v - r) / vm

        val g2 = (v - g) / vm

        val b2 = (v - b) / vm

        if (r == v)
            h = (if (g == m) 5.0 + b2 else 1.0 - g2)
        else if (g == v)
            h = (if (b == m) 1.0 + r2 else 3.0 - b2)
        else
            h = if (r == m) 3.0 + g2 else 5.0 - r2
        h /= 6
        PlSigMeshService.getInstance().setLightHSL(dst, (l * 65535).toShort(), (h * 65535).toShort(), (s * 65535).toShort(), Util.PL_DEFAULT_ONOFF_TRANSITIONTIME, Util.PL_DEFAULT_ONOFF_DELAY, Util.PL_DEFAULT_APP_KEY_INDEX, true)

    }
}