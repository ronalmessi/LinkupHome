package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.SettingDao
import com.ihomey.linkuphome.data.db.ZoneDao
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class ZoneRepository @Inject constructor(private val zoneDao: ZoneDao, private val settingDao: SettingDao, private var appExecutors: AppExecutors) {

    fun getZones(): LiveData<Resource<List<Zone>>> {
        return object : NetworkBoundResource<List<Zone>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Zone>> {
                return zoneDao.getZones()
            }
        }.asLiveData()
    }

    fun getZone(id:Int): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun loadFromDb(): LiveData<Zone> {
                return zoneDao.getZone(id)
            }
        }.asLiveData()
    }


    fun insert(zone: Zone, isCurrent: Boolean) {
        appExecutors.diskIO().execute {
            val id = zoneDao.insert(zone)
            if (isCurrent) {
                var currentZoneId by PreferenceHelper("currentZoneId", -1)
                currentZoneId = id.toInt()
            }
        }
    }


    fun updateZoneName(newName: String, id: Int) {
        appExecutors.diskIO().execute {
            zoneDao.updateZoneName(newName, id)
        }
    }

    fun setCurrentZone(id: Int) {
        appExecutors.diskIO().execute {
//            zoneDao.deleteCurrentZone()
//            zoneDao.setCurrentZone(id)
//            settingDao.updateZoneId(id)
        }
    }

    fun deleteZone(zoneId: Int) {
        appExecutors.diskIO().execute {
            zoneDao.delete(zoneId)
        }
    }
}



