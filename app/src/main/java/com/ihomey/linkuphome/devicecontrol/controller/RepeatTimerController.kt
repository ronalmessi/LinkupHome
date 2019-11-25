package com.ihomey.linkuphome.devicecontrol.controller

interface RepeatTimerController {

     fun setRepeatTimer( minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int)

     fun syncTime()
}