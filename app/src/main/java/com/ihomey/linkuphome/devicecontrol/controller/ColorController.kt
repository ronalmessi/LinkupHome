package com.ihomey.linkuphome.devicecontrol.controller


interface ColorController {

      fun setColor(colorValue: String)

      fun setLightingMode()

      fun setCycleMode(cycleSpeed: Int)

}