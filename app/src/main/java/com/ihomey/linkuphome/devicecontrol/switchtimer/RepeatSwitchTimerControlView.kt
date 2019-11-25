package com.ihomey.linkuphome.devicecontrol.switchtimer

import android.view.View
import android.view.ViewStub
import android.widget.CheckBox
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.devicecontrol.controller.RepeatTimerController
import com.ihomey.linkuphome.devicecontrol.controller.repeatcontroller.RepeatTimerControllerFactory
import java.util.*

class RepeatSwitchTimerControlView(val parentView: View, val device: Device) : BaseSwitchTimerControlView(parentView) {

    private lateinit var checkBox: CheckBox

    private lateinit var controller: RepeatTimerController

    override fun saveTime(hour: Int, minute: Int, isOpenTimer: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        if (isOpenTimer) {
            mLocalState.openTimer = calendar.timeInMillis
            mLocalState.openTimerOn = 1
            if (checkBox.isChecked) {
                mLocalState.openDayOfWeek = 1000
            } else {
                mLocalState.openDayOfWeek = -1000
            }
//            controller.setRepeatTimer(minute, hour, isOpenTimer = true, isOn = true, repeatMode = if (checkBox.isChecked) 1000 else -1000)
        } else {
            mLocalState.closeTimer = calendar.timeInMillis
            mLocalState.closeTimerOn = 1
            if (checkBox.isChecked) {
                mLocalState.closeDayOfWeek = 1000
            } else {
                mLocalState.closeDayOfWeek = -1000
            }
//            controller.setRepeatTimer(minute, hour, isOpenTimer = false, isOn = true, repeatMode = if (checkBox.isChecked) 1000 else -1000)
        }
        getOnTimerChangedListener()?.onTimerChanged(mLocalState)
    }

    override fun setTimerEditable(isEditable: Boolean, isOpenTimer: Boolean) {
        radioGroupPlus.visibility = if (isEditable) View.GONE else View.VISIBLE
        checkBox.visibility = if (isEditable) View.VISIBLE else View.GONE
        val repeatMode = if (isOpenTimer) mLocalState.openDayOfWeek else mLocalState.closeDayOfWeek
        checkBox.isChecked = repeatMode == 1000

    }

    override fun onSwitchStateChange(isOn: Boolean, isOpenTimer: Boolean) {
        if (isOpenTimer) {
            mLocalState.openTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.openTimer)
            controller.setRepeatTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), true, isOn, repeatMode = if (checkBox.isChecked) 1000 else -1000)
        } else {
            mLocalState.closeTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.closeTimer)
            controller.setRepeatTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), true, isOn, repeatMode = if (checkBox.isChecked) 1000 else -1000)
        }
        getOnTimerChangedListener()?.onTimerChanged(mLocalState)

    }

    override fun bindTo(localState: LocalState?) {
        localState?.let { this.mLocalState = it }
        this.controller = RepeatTimerControllerFactory().createController(device)
        controller.syncTime()
        val viewStub=parentView.findViewById<ViewStub>(R.id.view_stub_repeat)
        if(viewStub!=null){
            val view = viewStub.inflate()
            checkBox = view.findViewById(R.id.cb_timer_setting_repeat)
        }
    }
}