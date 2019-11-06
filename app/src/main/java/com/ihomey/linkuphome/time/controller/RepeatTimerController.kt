package com.ihomey.linkuphome.time.controller

import com.ihomey.linkuphome.data.entity.Device


interface RepeatTimerController {

     fun setRepeatTimer( minute: Int, hour: Int, isOpenTimer: Boolean, isOn: Boolean, repeatMode: Int)
}