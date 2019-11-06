package com.ihomey.linkuphome.switchtimer.repeatcontroller

interface RepeatTimerController {

     fun setRepeatTimer( minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int)

     fun syncTime()
}