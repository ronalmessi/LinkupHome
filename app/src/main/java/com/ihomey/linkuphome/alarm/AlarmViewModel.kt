package com.ihomey.linkuphome.alarm

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.RemoveDeviceVo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class AlarmViewModel : ViewModel() {


    private val mDeviceId = MutableLiveData<String>()

     val mAlarm = MutableLiveData<Alarm>()

    @Inject
    lateinit var alarmRepository: AlarmRepository

    val mAlarmsResult:  LiveData<Resource<List<Alarm>>>

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

    fun saveAlarm(alarm: Alarm){
        return alarmRepository.saveAlarm(alarm)
    }

    fun deleteAlarm(alarm: Alarm){
        return alarmRepository.deleteAlarm(alarm)
    }
}
