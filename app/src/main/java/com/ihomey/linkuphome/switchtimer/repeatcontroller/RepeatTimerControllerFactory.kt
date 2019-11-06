package com.ihomey.linkuphome.switchtimer.repeatcontroller

import com.ihomey.linkuphome.data.entity.Device

class RepeatTimerControllerFactory {

    fun createController(device: Device): RepeatTimerController {
        return when {
            device.pid!=0 -> SigMeshV1RepeatTimerController(device)
            device.instructId!=0 -> CSRMeshV1RepeatTimerController(device)
            else -> M1RepeatTimerController(device)
        }
    }
}