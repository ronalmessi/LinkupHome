package com.ihomey.linkuphome.alarm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject


/**
 * Created by dongcaizheng on 2018/4/9.
 */
class AlarmViewModel : ViewModel() {

    private val mAlarm = MutableLiveData<Alarm>()

    @Inject
    lateinit var repository: AlarmRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }


    fun setAlarm(alarm: Alarm) {
        mAlarm.value = alarm
    }


    fun getAlarm(): MutableLiveData<Alarm> {
        return mAlarm
    }


    fun setDayOfWeek(value: Int) {
        val alarm = mAlarm.value
        alarm?.dayOfWeek = value
        mAlarm.value = alarm
    }

    fun setTime(hour: Int, minute: Int) {
        val alarm = mAlarm.value
        alarm?.hour = hour
        alarm?.minute = minute
        mAlarm.value = alarm
    }

    fun setRingType(value: Int) {
        val alarm = mAlarm.value
        alarm?.ringType = value
        mAlarm.value = alarm
    }

    fun setType(value: Int,hour: Int, minute: Int) {
        val alarm = mAlarm.value
        alarm?.hour = hour
        alarm?.minute = minute
        alarm?.type = value
        mAlarm.value = alarm
    }


    fun saveAlarm(alarm: Alarm) {
        repository.saveAlarm(alarm)
    }

    fun updateAlarm(alarm: Alarm) {
        repository.updateAlarm(alarm)

    }

    fun deleteAlarm(alarm: Alarm) {
        repository.deleteAlarm(alarm)

    }


}