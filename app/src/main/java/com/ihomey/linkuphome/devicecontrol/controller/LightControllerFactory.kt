package com.ihomey.linkuphome.devicecontrol.controller

import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.impl.*

/**
 * Created by Administrator on 2017/6/16.
 */
class LightControllerFactory {

    fun createCommonController(device: Device): CommonController? {
        return when(device.type) {
            0 -> M1LightController(device)
            1 -> C3Controller(device)
            2 -> R2Controller(device)
            3 -> A2Controller(device)
            4 -> N1Controller(device)
            6 -> if(device.pid==0) V1Controller(device) else SigMeshV1Controller(device)
            7 -> R2Controller(device)
            8 -> A2Controller(device)
            9 -> R2Controller(device)
            10 -> V2Controller(device)
            else ->null
        }
    }

    fun createColorController(device: Device): ColorController? {
        return when(device.type) {
            0 -> M1LightController(device)
            1 -> C3Controller(device)
            2 -> R2Controller(device)
            4 -> N1Controller(device)
            6 -> if(device.pid==0) V1Controller(device) else SigMeshV1Controller(device)
            7 -> R2Controller(device)
            9 -> R2Controller(device)
            10 -> V2Controller(device)
            else ->null
        }
    }

    fun createColorTemperatureController(device: Device): ColorTemperatureController? {
        return when(device.type) {
            0 -> M1LightController(device)
            2 -> R2Controller(device)
            7 -> R2Controller(device)
            9 -> R2Controller(device)
            else ->null
        }
    }

    fun createColorSceneController(device: Device): SceneController? {
        return when(device.type) {
            0 -> M1LightController(device)
            2 -> R2Controller(device)
            4 -> N1Controller(device)
            6 -> if(device.pid==0) V1Controller(device) else SigMeshV1Controller(device)
            7 -> R2Controller(device)
            9 -> R2Controller(device)
            10 -> V2Controller(device)
            else ->null
        }
    }

    fun createTimerController(device: Device): TimerController? {
        return when {
            device.instructId!=0&&(device.type==2||device.type==7||device.type==9) -> R2Controller(device)
            device.instructId!=0&&(device.type==3||device.type==8) -> A2Controller(device)
            device.instructId!=0&&device.type==4 -> N1Controller(device)
            else ->null
        }
    }
}