package com.ihomey.linkuphome.controller

/**
 * Created by Administrator on 2017/6/16.
 */
class ControllerFactory {

    fun createController(type: Int): Controller? {
        return when (type) {
            4 -> LawnController()
            3 -> RgbController()
            2 -> ColorTemperatureController()
            0 -> LedController()
            1-> OutdoorController()
            5 -> RgbController()
            6 -> ColorTemperatureController()
            else -> null
        }
    }
}