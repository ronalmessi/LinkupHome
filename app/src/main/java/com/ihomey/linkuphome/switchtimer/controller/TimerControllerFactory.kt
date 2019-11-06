package com.ihomey.linkuphome.switchtimer.controller

import com.ihomey.linkuphome.data.entity.Device

class TimerControllerFactory {

    fun createController(device: Device):TimerController? {
        return when {
            device.instructId!=0&&(device.type==2||device.type==7||device.type==9) -> R2TimerController(device)
            device.instructId!=0&&(device.type==3||device.type==8) -> A2TimerController(device)
            device.instructId!=0&&device.type==4 -> N1TimerController(device)
            else ->null
        }
    }
}