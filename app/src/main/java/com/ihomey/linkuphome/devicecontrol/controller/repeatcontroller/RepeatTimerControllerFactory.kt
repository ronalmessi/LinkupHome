package com.ihomey.linkuphome.devicecontrol.controller.repeatcontroller

import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.RepeatTimerController

class RepeatTimerControllerFactory {

    fun createController(device: Device): RepeatTimerController {
        return when {
            device.pid!=0 -> SigMeshV1RepeatTimerController(device)
            device.instructId!=0 -> CSRMeshV1RepeatTimerController(device)
            else -> M1RepeatTimerController(device)
        }
    }
}