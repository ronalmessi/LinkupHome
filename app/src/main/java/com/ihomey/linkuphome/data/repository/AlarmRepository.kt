package com.ihomey.linkuphome.data.repository


import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.AlarmDao
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class AlarmRepository @Inject constructor(private val alarmDao: AlarmDao, private var appExecutors: AppExecutors) {

    fun getAlarms(deviceId: String): LiveData<Resource<List<Alarm>>> {
        return object : DbBoundResource<List<Alarm>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Alarm>> {
                return alarmDao.getAlarms(deviceId)
            }
        }.asLiveData()
    }

    fun saveAlarm(alarm: Alarm) {
        appExecutors.diskIO().execute {
            alarmDao.insert(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        appExecutors.diskIO().execute {
            alarmDao.delete(alarm)
        }
    }

}



