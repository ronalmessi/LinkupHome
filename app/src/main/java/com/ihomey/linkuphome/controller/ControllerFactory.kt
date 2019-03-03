package com.ihomey.linkuphome.controller

/**
 * Created by Administrator on 2017/6/16.
 */
class ControllerFactory {

    fun createController(type: Int): Controller? {
        return when (type) {
            0 -> C3Controller()
            1 -> R2Controller()
            2 -> A2Controller()
            3 -> N1Controller()
            4 -> V1Controller()
            5 -> V1Controller()
            6 -> R2Controller()
            7 -> A2Controller()
            else -> null
        }
    }
}