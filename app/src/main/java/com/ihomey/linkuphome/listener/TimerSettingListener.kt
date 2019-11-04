package com.ihomey.linkuphome.listener


/**
 * Created by Administrator on 2017/6/21.
 */
interface TimerSettingListener {
    fun saveTime(hour: Int, minute: Int)
    fun enableEditTimer(flag: Boolean)
    fun updateTimerOnState(isChecked: Boolean)
}

