package com.ihomey.linkuphome.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.data.api.AbsentLiveData
import com.ihomey.linkuphome.data.api.ApiResult
import com.ihomey.linkuphome.data.api.ApiService
import com.ihomey.linkuphome.data.api.NetworkBoundResource
import com.ihomey.linkuphome.data.db.SettingDao
import com.ihomey.linkuphome.data.db.ZoneDao
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.ZoneVO
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class ZoneRepository @Inject constructor(private var apiService: ApiService, private val zoneDao: ZoneDao, private val settingDao: SettingDao, private var appExecutors: AppExecutors) {

    fun createZone(guid:String,name:String): LiveData<Resource<Boolean>> {
        return object : NetworkBoundResource<Boolean>(appExecutors) {
            override fun saveCallResult(item: Boolean) {

            }

            override fun shouldFetch(data: Boolean?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Boolean> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Boolean>> {
                val zoneVo=ZoneVO(guid,name,System.currentTimeMillis())
                zoneVo.signature= beanToJson(zoneVo).sha256()
                return apiService.createZone(zoneVo)
            }
        }.asLiveData()
    }


    fun getZones(): LiveData<Resource<List<Zone>>> {
        return object : DbBoundResource<List<Zone>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Zone>> {
                return zoneDao.getZones()
            }
        }.asLiveData()
    }

    fun getZone(id: Int): LiveData<Resource<Zone>> {
        return object : DbBoundResource<Zone>(appExecutors) {
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

    fun deleteZone(zoneId: Int) {
        appExecutors.diskIO().execute {
            var currentZoneId by PreferenceHelper("currentZoneId", -1)
            zoneDao.delete(zoneId)
            if (currentZoneId == zoneId) {
                 val minZoneId=zoneDao.getMinZoneId()
                currentZoneId=minZoneId
            }
        }
    }
}



