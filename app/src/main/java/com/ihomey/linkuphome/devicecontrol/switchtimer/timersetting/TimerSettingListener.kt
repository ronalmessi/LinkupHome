package com.ihomey.linkuphome.devicecontrol.switchtimer.timersetting



interface TimerSettingListener {
    fun saveTime(hour: Int, minute: Int,isOpenTimer:Boolean)
    fun setTimerEditable(isEditable: Boolean,isOpenTimer:Boolean)
    fun onSwitchStateChange(isOn: Boolean,isOpenTimer:Boolean)
}

