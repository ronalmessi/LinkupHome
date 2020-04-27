package com.ihomey.linkuphome.devicecontrol.controller.impl

import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.ColorController
import com.ihomey.linkuphome.devicecontrol.controller.CommonController
import com.ihomey.linkuphome.devicecontrol.controller.SceneController
import com.ihomey.linkuphome.devicecontrol.controller.TimerController
import com.ihomey.linkuphome.getPeriodMinute
import com.pairlink.sigmesh.lib.PlSigMeshService
import com.pairlink.sigmesh.lib.Util
import java.util.*


class SigMeshV1Controller(val device: Device) : CommonController, ColorController, SceneController, TimerController {

    var featureValue by PreferenceHelper("feature_" + device.id, 0)

    override fun setBrightness(brightness: Int) {
        val hexString = Integer.toHexString(brightness + 16021)
        val lsb = hexString.takeLast(2)
        val msb = hexString.take(2)
        if (PlSigMeshService.getInstance().isMeshReady) {
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB2$msb$lsb"), Util.PL_DEFAULT_APP_KEY_INDEX)
            PreferenceHelper("feature_" + device.id, 0).deletePreference("feature_" + device.id)
        }
    }

    override fun setOnOff(isOn: Boolean) {
        if (PlSigMeshService.getInstance().isMeshReady) {
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes((if (isOn) "7FB164" else "7FB100")), Util.PL_DEFAULT_APP_KEY_INDEX)
            if (featureValue == 1 && !isOn) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes((if (isOn) "7FB164" else "7FB100")), Util.PL_DEFAULT_APP_KEY_INDEX)
                        PreferenceHelper("feature_" + device.id, 0).deletePreference("feature_" + device.id)
                    }
                }, 100)
            }
        }
    }

    override fun setColor(colorValue: String) {
        val rgb = AppConfig.RGB_COLOR[Integer.parseInt(colorValue, 16)]
        val r = if (rgb.r >= 16) Integer.toHexString(rgb.r) else "0" + Integer.toHexString(rgb.r)
        val g = if (rgb.g >= 16) Integer.toHexString(rgb.g) else "0" + Integer.toHexString(rgb.g)
        val b = if (rgb.b >= 16) Integer.toHexString(rgb.b) else "0" + Integer.toHexString(rgb.b)
        if (PlSigMeshService.getInstance().isMeshReady) {
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB302$r$g$b"), Util.PL_DEFAULT_APP_KEY_INDEX)
            PreferenceHelper("feature_" + device.id, 0).deletePreference("feature_" + device.id)
        }
    }

    override fun setLightingMode() {
        if (PlSigMeshService.getInstance().isMeshReady) {
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB3010FA0"), Util.PL_DEFAULT_APP_KEY_INDEX)
            if (featureValue == 1) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB3010FA0"), Util.PL_DEFAULT_APP_KEY_INDEX)
                        PreferenceHelper("feature_" + device.id, 0).deletePreference("feature_" + device.id)
                    }
                }, 100)
            }
        }
    }

    override fun setCycleMode(cycleSpeed: Int) {
        if (PlSigMeshService.getInstance().isMeshReady) {
            featureValue = 1
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB4010" + ((2 - cycleSpeed)*4+1)), Util.PL_DEFAULT_APP_KEY_INDEX)
        }
    }

    override fun setScene(sceneValue: Int) {
        if (PlSigMeshService.getInstance().isMeshReady) {
            if (sceneValue == 2) {
                featureValue = 1
            }
            if(device.type==6||device.type==10){
                PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB403F" + (sceneValue + 1)), Util.PL_DEFAULT_APP_KEY_INDEX)
            }else{
                PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB402F$sceneValue"), Util.PL_DEFAULT_APP_KEY_INDEX)
            }
        }
    }

    override fun setTimer(minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean) {
        var command="7FB501" + if(isOpenTimer)"01" else "02"
        command += if(isOn){
            String.format("%04x", getPeriodMinute(hour, minute))
        }else{
            "0000"
        }
        if (PlSigMeshService.getInstance().isMeshReady) {
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes(command), Util.PL_DEFAULT_APP_KEY_INDEX)
        }
    }

}
