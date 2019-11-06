package com.ihomey.linkuphome.time.controller

import com.ihomey.linkuphome.data.entity.Device
import com.pairlink.sigmesh.lib.PlSigMeshService
import com.pairlink.sigmesh.lib.Util

class SigMeshRepeatTimerController(val device: Device): RepeatTimerController {

    override fun setRepeatTimer(minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int) {
        val command = "7FB502" + if (isOpenTimer) "01" else "02" + if (isOn) (if (repeatMode==1000) "FF" else "80") else "00" + String.format("%02d",hour)+String.format("%02d",minute)
        if(PlSigMeshService.getInstance().isMeshReady)  PlSigMeshService.getInstance().vendorUartSend(device.instructId.toShort(), Util.hexStringToBytes(command), Util.PL_DEFAULT_APP_KEY_INDEX)
    }

}