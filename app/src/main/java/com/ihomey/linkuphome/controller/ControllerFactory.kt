package com.ihomey.linkuphome.controller

/**
 * Created by Administrator on 2017/6/16.
 */
class ControllerFactory {

    fun createController(type: Int, isSigMesh: Boolean): Controller? {
        return when (type) {
            1 -> C3Controller()
            2 -> R2Controller()
            3 -> A2Controller()
            4 -> N1Controller()
            0 -> M1Controller()
            6 -> if (isSigMesh) SigMeshController() else V1Controller()
            7 -> R2Controller()
            8 -> A2Controller()
            9 -> R2Controller()
            10 -> V2Controller()
            else -> null
        }
    }
}