package com.ihomey.linkuphome.devicecontrol.controller.repeatcontroller

import android.util.Log
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.RepeatTimerController
import com.pairlink.sigmesh.lib.PlSigMeshService
import com.pairlink.sigmesh.lib.Util
import java.util.*

class SigMeshV1RepeatTimerController(val device: Device): RepeatTimerController {

    override fun syncTime() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val dateCommand = "7FB5F101" + String.format("%02d",calendar.get(Calendar.YEAR) % 2000)+(calendar.get(Calendar.MONTH) + 1)+(calendar.get(Calendar.DAY_OF_WEEK) - 1)+ String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH))
        Log.d("aa","---"+ dateCommand)
        if(PlSigMeshService.getInstance().isMeshReady)  PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes(dateCommand), Util.PL_DEFAULT_APP_KEY_INDEX)
        val timeCommand = "7FB5F102" + String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY))+ String.format("%02d",calendar.get(Calendar.MINUTE))+ String.format("%02d",calendar.get(Calendar.SECOND))
        Log.d("aa","---"+ timeCommand)
        if(PlSigMeshService.getInstance().isMeshReady)  PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes(timeCommand), Util.PL_DEFAULT_APP_KEY_INDEX)
    }

    override fun setRepeatTimer(minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int) {
        Log.d("aa","---"+ minute+"---"+hour+"---"+isOpenTimer+"---"+isOn+"---"+repeatMode)
        val command = "7FB502" + (if (isOpenTimer) "01" else "02") + (if (isOn) (if (repeatMode==1000) "FF" else "80") else "00") + String.format("%02d",hour)+String.format("%02d",minute)
        Log.d("aa","---"+ command)
        if(PlSigMeshService.getInstance().isMeshReady)  PlSigMeshService.getInstance().vendorUartSend(device.pid.toShort(), Util.hexStringToBytes(command), Util.PL_DEFAULT_APP_KEY_INDEX)
    }

}