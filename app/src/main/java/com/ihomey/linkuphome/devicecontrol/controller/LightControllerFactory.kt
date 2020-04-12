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
            1 -> if(device.pid==0) C3Controller(device) else SigMeshV1Controller(device)
            2 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            3 -> if(device.pid==0) A2Controller(device) else SigMeshV1Controller(device)
            4 -> if(device.pid==0) N1Controller(device) else SigMeshV1Controller(device)
            6 -> if(device.pid==0) V1Controller(device) else SigMeshV1Controller(device)
            7 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            8 -> if(device.pid==0) A2Controller(device) else SigMeshV1Controller(device)
            9 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            10 ->  if(device.pid==0) V2Controller(device) else SigMeshV1Controller(device)
            else ->null
        }
    }

    fun createColorController(device: Device): ColorController? {
        return when(device.type) {
            0 -> M1LightController(device)
            1 -> if(device.pid==0) C3Controller(device) else SigMeshV1Controller(device)
            2 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            4 -> if(device.pid==0) N1Controller(device) else SigMeshV1Controller(device)
            6 -> if(device.pid==0) V1Controller(device) else SigMeshV1Controller(device)
            7 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            9 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            10 -> if(device.pid==0) V2Controller(device) else SigMeshV1Controller(device)
            else ->null
        }
    }


    fun createColorTemperatureController(device: Device): ColorTemperatureController? {
        return when(device.type) {
            0 -> M1LightController(device)
            3,8 -> A2Controller(device)
            9 -> R2Controller(device)
            else ->null
        }
    }


    fun createColorSceneController(device: Device): SceneController? {
        return when(device.type) {
            0 -> M1LightController(device)
            2 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            4 -> if(device.pid==0) N1Controller(device) else SigMeshV1Controller(device)
            6 -> if(device.pid==0) V1Controller(device) else SigMeshV1Controller(device)
            7 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            9 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            10 -> if(device.pid==0) V2Controller(device) else SigMeshV1Controller(device)
            else ->null
        }
    }

    fun createTimerController(device: Device): TimerController? {
        return when(device.type) {
            2,7,9 -> if(device.pid==0) R2Controller(device) else SigMeshV1Controller(device)
            3,8 -> if(device.pid==0) A2Controller(device) else SigMeshV1Controller(device)
            4 -> if(device.pid==0) N1Controller(device) else SigMeshV1Controller(device)
            else ->null
        }
    }
}