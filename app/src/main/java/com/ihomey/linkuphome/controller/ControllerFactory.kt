package com.ihomey.linkuphome.controller

/**
 * Created by Administrator on 2017/6/16.
 */
class ControllerFactory {

    fun createController(type: Int): Controller? {
        return when (type) {
            0 -> LawnController()
            1 -> RgbController()
            2 -> ColorTemperatureController()
            3 -> LedController()
            5 -> MiniOutdoorController()
            6 -> RgbController()
            7 -> ColorTemperatureController()
            8 -> RgbController()
            else -> null
        }
    }
}