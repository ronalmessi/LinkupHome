package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.ModelDao
import com.ihomey.linkuphome.data.db.ZoneDao
import com.ihomey.linkuphome.data.vo.Model
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Zone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class ZoneRepository @Inject constructor(private val zoneDao: ZoneDao, private var appExecutors: AppExecutors) {

    fun getZones(): LiveData<Resource<List<Zone>>> {
        return object : NetworkBoundResource<List<Zone>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Zone>> {
                return zoneDao.getZones()
            }
        }.asLiveData()
    }

    fun getCurrentZone(): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun loadFromDb(): LiveData<Zone> {
                return zoneDao.getCurrentZone()
            }
        }.asLiveData()
    }

    fun insert(zone: Zone) {
        appExecutors.diskIO().execute {
            zoneDao.insert(zone)
        }
    }


    fun updateZoneName(newName: String, id: Int) {
        appExecutors.diskIO().execute {
            zoneDao.updateZoneName(newName,id)
        }
    }

    fun setCurrentZone(id: Int) {
        appExecutors.diskIO().execute {
            zoneDao.deleteCurrentZone()
            zoneDao.setCurrentZone(id)
        }
    }

//    fun deleteModel(deviceId: Int,groupId: Int,groupIndex:Int) {
//        appExecutors.diskIO().execute {
//            modelDao.deleteModel(deviceId, groupId, groupIndex)
//        }
//    }
}



