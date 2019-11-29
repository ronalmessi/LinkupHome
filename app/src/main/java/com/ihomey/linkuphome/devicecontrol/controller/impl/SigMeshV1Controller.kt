package com.ihomey.linkuphome.devicecontrol.controller.impl

import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.ColorController
import com.ihomey.linkuphome.devicecontrol.controller.CommonController
import com.ihomey.linkuphome.devicecontrol.controller.SceneController
import com.pairlink.sigmesh.lib.PlSigMeshService
import com.pairlink.sigmesh.lib.Util
import java.util.*


class SigMeshV1Controller(val device: Device) : CommonController, ColorController, SceneController {

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
            if (featureValue == 2 && !isOn) {
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
            if (featureValue == 2) {
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
            featureValue = 2
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB4010" + (3 - cycleSpeed)), Util.PL_DEFAULT_APP_KEY_INDEX)
        }
    }

    override fun setScene(sceneValue: Int) {
        if (PlSigMeshService.getInstance().isMeshReady) {
            if (sceneValue == 2) {
                featureValue = 1
            }
            PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes("7FB403F" + (sceneValue + 1)), Util.PL_DEFAULT_APP_KEY_INDEX)
        }
    }
}
