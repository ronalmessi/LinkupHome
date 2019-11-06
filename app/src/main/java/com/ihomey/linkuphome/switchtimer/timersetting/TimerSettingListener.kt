package com.ihomey.linkuphome.switchtimer.timersetting



interface TimerSettingListener {
    fun saveTime(hour: Int, minute: Int)
    fun setTimerEditable(isEditable: Boolean)
    fun onSwitchStateChange(isOn: Boolean)
}

