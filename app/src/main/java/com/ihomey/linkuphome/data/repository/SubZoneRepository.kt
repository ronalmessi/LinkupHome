package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.SubZoneDao
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.SubZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class SubZoneRepository @Inject constructor(private val subZoneDao: SubZoneDao, private var appExecutors: AppExecutors) {

    fun getSubZones(deviceId: Int): LiveData<Resource<List<SubZone>>> {
        return object : NetworkBoundResource<List<SubZone>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SubZone>> {
                return subZoneDao.getSubZones(deviceId)
            }
        }.asLiveData()
    }


    fun insert(subZone: SubZone) {
        appExecutors.diskIO().execute {
            subZoneDao.insert(subZone)
        }
    }

    fun delete(id: Int) {
        appExecutors.diskIO().execute {
            subZoneDao.delete(id)
        }
    }
}



