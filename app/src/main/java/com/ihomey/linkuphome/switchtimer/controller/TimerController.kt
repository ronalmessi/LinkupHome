package com.ihomey.linkuphome.switchtimer.controller


interface TimerController {
     fun setTimer( minute: Int, hour: Int,isOpenTimer: Boolean, isOn: Boolean)
}