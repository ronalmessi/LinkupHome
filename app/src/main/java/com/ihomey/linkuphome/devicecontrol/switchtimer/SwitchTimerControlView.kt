package com.ihomey.linkuphome.devicecontrol.switchtimer

import android.view.View
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.devicecontrol.controller.TimerController
import java.util.*

class SwitchTimerControlView(parentView: View, val device: Device) : BaseSwitchTimerControlView(parentView) {

    private  var controller: TimerController?=null

    override fun saveTime(hour: Int, minute: Int, isOpenTimer: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val isExpired = calendar.timeInMillis - System.currentTimeMillis() < 0
        if (isOpenTimer) {
            mLocalState.openTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
            mLocalState.openTimerOn = 1
//            controller?.setTimer(minute, hour, isOpenTimer = true, isOn = true)
        } else {
            mLocalState.closeTimerOn = 1
            mLocalState.closeTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
//            controller?.setTimer(minute, hour, isOpenTimer = false, isOn = false)
        }
        getOnTimerChangedListener()?.onTimerChanged(mLocalState)
    }

    override fun setTimerEditable(isEditable: Boolean, isOpenTimer: Boolean) {

    }

    override fun onSwitchStateChange(isOn: Boolean, isOpenTimer: Boolean) {
        if (isOpenTimer) {
            mLocalState.openTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.openTimer)
            controller?.setTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), isOpenTimer =true, isOn = isOn)
        } else {
            mLocalState.closeTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.closeTimer)
            controller?.setTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), isOpenTimer =false, isOn = isOn)
        }
        getOnTimerChangedListener()?.onTimerChanged(mLocalState)
    }

    override fun bindTo(localState: LocalState?) {
        localState?.let { this.mLocalState = it }
        this.controller = LightControllerFactory().createTimerController(device)
    }
}