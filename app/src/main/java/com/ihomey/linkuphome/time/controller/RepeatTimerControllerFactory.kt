package com.ihomey.linkuphome.time.controller

import com.ihomey.linkuphome.data.entity.Device

class RepeatTimerControllerFactory {

    fun createController(device: Device): RepeatTimerController {
        return when {
            device.pid!=0 -> SigMeshRepeatTimerController(device)
            device.instructId!=0 -> CSRMeshRepeatTimerController(device)
            else -> M1RepeatTimerController(device)
        }
    }
}