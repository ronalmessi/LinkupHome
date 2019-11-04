package com.ihomey.linkuphome.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.data.repository.AlarmRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class AlarmViewModel : ViewModel() {


    private val mDeviceId = MutableLiveData<String>()

    val mAlarm = MutableLiveData<Alarm>()

    @Inject
    lateinit var alarmRepository: AlarmRepository

    val mAlarmsResult: LiveData<Resource<List<Alarm>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
        mAlarmsResult = Transformations.switchMap(mDeviceId) { input ->
            alarmRepository.getAlarms(input)
        }
    }

    fun setDeviceId(deviceId: String) {
        mDeviceId.value = deviceId
    }

    fun setCurrentAlarm(alarm: Alarm?) {
        mAlarm.value = alarm
    }

    fun saveAlarm(alarm: Alarm) {
        return alarmRepository.saveAlarm(alarm)
    }

    fun deleteAlarm(alarm: Alarm) {
        return alarmRepository.deleteAlarm(alarm)
    }
}
