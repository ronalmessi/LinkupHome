package com.ihomey.linkuphome.controller

/**
 * Created by Administrator on 2017/6/16.
 */
class ControllerFactory {

    fun createController(type: Int): Controller? {
        return when (type) {
            1 -> C3Controller()
            2 -> R2Controller()
            3 -> A2Controller()
            4 -> N1Controller()
            5 -> V1Controller()
            6 -> V1Controller()
            7 -> R2Controller()
            8 -> A2Controller()
            9 -> R2Controller()
            10 -> V1Controller()
            else -> null
        }
    }
}