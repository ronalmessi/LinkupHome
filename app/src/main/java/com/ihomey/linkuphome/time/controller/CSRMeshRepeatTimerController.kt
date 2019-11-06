package com.ihomey.linkuphome.time.controller

import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.chechSum
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex

class CSRMeshRepeatTimerController(val device: Device): RepeatTimerController {

    override fun setRepeatTimer(minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int) {
        val commandPrefix="C201F304"+ (if(isOpenTimer) "C5" else "C6")+(if (isOn) { if (repeatMode==1000) "80" else "00" } else "FF")+ if (!isOn) "0000" else String.format("%02d",hour)+String.format("%02d",minute)
        val command = commandPrefix + chechSum(commandPrefix.substring(6)) + "16"
        DataModelApi.sendData(device.instructId, decodeHex(command.toCharArray()), false)
    }

}