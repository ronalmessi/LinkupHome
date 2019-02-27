package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.SettingDao
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class SettingRepository @Inject constructor(private val settingDao: SettingDao, private var appExecutors: AppExecutors) {

    fun getSetting(): LiveData<Resource<Setting>> {
        return object : NetworkBoundResource<Setting>(appExecutors) {
            override fun loadFromDb(): LiveData<Setting> {
                return settingDao.getSetting()
            }
        }.asLiveData()
    }
}



