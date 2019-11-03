package com.ihomey.linkuphome.listener

import android.view.View
import com.ihomey.linkuphome.time.M1TimerSettingFragment
import kotlinx.android.synthetic.main.open_timer_setting_fragment.*


/**
 * Created by Administrator on 2017/6/21.
 */
interface TimerSettingListener {
    fun saveTime(hour:Int,minute:Int)
    fun enableEditTimer(flag: Boolean)
    fun updateTimerOnState(isChecked: Boolean)
}

