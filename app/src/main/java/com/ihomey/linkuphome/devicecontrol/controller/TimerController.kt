package com.ihomey.linkuphome.devicecontrol.controller


interface TimerController {
     fun setTimer( minute: Int, hour: Int,isOpenTimer: Boolean, isOn: Boolean)
}