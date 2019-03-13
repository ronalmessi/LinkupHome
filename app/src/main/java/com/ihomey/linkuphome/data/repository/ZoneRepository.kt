package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.data.api.AbsentLiveData
import com.ihomey.linkuphome.data.api.ApiResult
import com.ihomey.linkuphome.data.api.ApiService
import com.ihomey.linkuphome.data.api.NetworkBoundResource
import com.ihomey.linkuphome.data.db.SettingDao
import com.ihomey.linkuphome.data.db.ZoneDao
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class ZoneRepository @Inject constructor(private var apiService: ApiService, private val zoneDao: ZoneDao, private val settingDao: SettingDao, private var appExecutors: AppExecutors) {

    fun createZone(guid:String,name:String): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun saveCallResult(item: Zone) {
                zoneDao.insert(item)
            }

            override fun shouldFetch(data: Zone?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Zone> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Zone>> {
                val zoneVo=CreateZoneVO(guid,name,System.currentTimeMillis())
                zoneVo.signature= beanToJson(zoneVo).sha256()
                return apiService.createZone(zoneVo)
            }
        }.asLiveData()
    }

    fun changeZoneName(guid:String,zoneId:Int,newName:String): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun saveCallResult(item: Zone) {
                zoneDao.updateZoneName(newName, zoneId)
            }

            override fun shouldFetch(data: Zone?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Zone> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Zone>> {
                val changeZoneNameVO=ChangeZoneNameVO(guid,zoneId,newName,System.currentTimeMillis())
                changeZoneNameVO.signature= beanToJson(changeZoneNameVO).sha256()
                return apiService.changeZoneName(changeZoneNameVO)
            }
        }.asLiveData()
    }

    fun deleteZone(guid:String,zoneId:Int): LiveData<Resource<Boolean>> {
        return object : NetworkBoundResource<Boolean>(appExecutors) {
            override fun saveCallResult(item: Boolean) {
                  zoneDao.delete(zoneId)
            }

            override fun shouldFetch(data: Boolean?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Boolean> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Boolean>> {
                val deleteZoneVO=DeleteZoneVO(guid,zoneId,System.currentTimeMillis())
                deleteZoneVO.signature= beanToJson(deleteZoneVO).sha256()
                return apiService.deleteZone(deleteZoneVO)
            }
        }.asLiveData()
    }

    fun shareZone(guid:String,zoneId:Int): LiveData<Resource<String>> {
        return object : NetworkBoundResource<String>(appExecutors) {
            override fun saveCallResult(item: String) {
                zoneDao.delete(zoneId)
            }

            override fun shouldFetch(data: String?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<String> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<String>> {
                val deleteZoneVO=DeleteZoneVO(guid,zoneId,System.currentTimeMillis())
                deleteZoneVO.signature= beanToJson(deleteZoneVO).sha256()
                return apiService.shareZone(deleteZoneVO)
            }
        }.asLiveData()
    }

    fun joinZone(guid:String,invitationCode:String): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun saveCallResult(item: Zone) {
                zoneDao.insert(item)
            }

            override fun shouldFetch(data: Zone?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Zone> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Zone>> {
                val baseVo=JoinZoneVO(invitationCode,guid,System.currentTimeMillis())
                baseVo.signature= beanToJson(baseVo).sha256()
                return apiService.joinZone(baseVo)
            }
        }.asLiveData()
    }


    fun getCurrentZone(guid:String): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun saveCallResult(item: Zone) {
                zoneDao.insert(item)
            }

            override fun shouldFetch(data: Zone?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Zone> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Zone>> {
                val baseVo=BaseVO(guid,System.currentTimeMillis())
                baseVo.signature= beanToJson(baseVo).sha256()
                return apiService.getCurrentZone(baseVo)
            }
        }.asLiveData()
    }


    fun getZones(guid:String): LiveData<Resource<List<Zone>>> {
        return object : NetworkBoundResource<List<Zone>>(appExecutors) {
            override fun saveCallResult(item: List<Zone>) {
//                zoneDao.insert(item)
            }

            override fun shouldFetch(data: List<Zone>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Zone>> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<List<Zone>>> {
                val baseVo=BaseVO(guid,System.currentTimeMillis())
                baseVo.signature= beanToJson(baseVo).sha256()
                return apiService.getZones(baseVo)
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

    fun getCurrentZoneId(): LiveData<Resource<Int>> {
        return object : DbBoundResource<Int>(appExecutors) {
            override fun loadFromDb(): LiveData<Int> {
                return zoneDao.getCurrentZoneId()
            }
        }.asLiveData()
    }
}



