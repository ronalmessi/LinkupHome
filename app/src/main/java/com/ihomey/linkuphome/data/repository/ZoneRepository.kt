package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.data.api.AbsentLiveData
import com.ihomey.linkuphome.data.api.ApiResult
import com.ihomey.linkuphome.data.api.ApiService
import com.ihomey.linkuphome.data.api.NetworkBoundResource
import com.ihomey.linkuphome.data.db.RoomDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.db.ZoneDao
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class ZoneRepository @Inject constructor(private var apiService: ApiService, private val zoneDao: ZoneDao, private val singleDeviceDao: SingleDeviceDao, private val roomDao: RoomDao,private var appExecutors: AppExecutors) {

    fun createZone(guid:String,name:String): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun saveCallResult(item: Zone?) {
                item?.let {  zoneDao.insert(it) }
            }

            override fun shouldFetch(data: Zone?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Zone> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Zone>> {
                val zoneVo=CreateZoneVO(guid.md5(),name,System.currentTimeMillis())
                zoneVo.signature= beanToJson(zoneVo).sha256()
                return apiService.createZone(zoneVo)
            }
        }.asLiveData()
    }

    fun changeZoneName(guid:String,zoneId:Int,newName:String): LiveData<Resource<Zone>> {
        return object : NetworkBoundResource<Zone>(appExecutors) {
            override fun saveCallResult(item: Zone?) {
                zoneDao.updateZoneName(newName, zoneId)
            }

            override fun shouldFetch(data: Zone?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Zone> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Zone>> {
                val changeZoneNameVO=ChangeZoneNameVO(guid.md5(),zoneId,newName,System.currentTimeMillis())
                changeZoneNameVO.signature= beanToJson(changeZoneNameVO).sha256()
                return apiService.changeZoneName(changeZoneNameVO)
            }
        }.asLiveData()
    }

    fun deleteZone(guid:String,zoneId:Int): LiveData<Resource<Int>> {
        return object : NetworkBoundResource<Int>(appExecutors) {
            override fun saveCallResult(item: Int?) {
                  zoneDao.delete(zoneId)
                  singleDeviceDao.deleteAll(zoneId)
                  roomDao.deleteAll(zoneId)
                  item?.let { zoneDao.activeZone(it) }
            }

            override fun shouldFetch(data: Int?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Int> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Int>> {
                val deleteZoneVO=DeleteVO(guid.md5(),zoneId,System.currentTimeMillis())
                deleteZoneVO.signature= beanToJson(deleteZoneVO).sha256()
                return apiService.deleteZone(deleteZoneVO)
            }
        }.asLiveData()
    }

    fun switchZone(guid:String,zoneId:Int): LiveData<Resource<ZoneDetail>> {
        return object : NetworkBoundResource<ZoneDetail>(appExecutors) {
            override fun saveCallResult(item: ZoneDetail?) {
                item?.let {
                    zoneDao.resetAllActiveZone()
                    zoneDao.insert(Zone(it.id,it.name,it.netWorkKey,it.nextDeviceIndex,it.nextGroupIndex,it.active))
                    singleDeviceDao.deleteAll(it.id)
                    roomDao.deleteAll(it.id)
                    it.devices?.let {it2 -> singleDeviceDao.insertAll(it2)}
                    it.groups?.let { it1 -> roomDao.insertAll(it1)}
                }
            }

            override fun shouldFetch(data: ZoneDetail?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<ZoneDetail> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<ZoneDetail>> {
                val deleteZoneVO=DeleteVO(guid.md5(),zoneId,System.currentTimeMillis())
                deleteZoneVO.signature= beanToJson(deleteZoneVO).sha256()
                return apiService.switchZone(deleteZoneVO)
            }
        }.asLiveData()
    }

    fun getZone(guid:String,zoneId:Int): LiveData<Resource<ZoneDetail>> {
        return object : NetworkBoundResource<ZoneDetail>(appExecutors) {
            override fun saveCallResult(item: ZoneDetail?) {

            }

            override fun shouldFetch(data: ZoneDetail?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<ZoneDetail> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<ZoneDetail>> {
                val deleteZoneVO=DeleteVO(guid.md5(),zoneId,System.currentTimeMillis())
                deleteZoneVO.signature= beanToJson(deleteZoneVO).sha256()
                return apiService.getZone(deleteZoneVO)
            }
        }.asLiveData()
    }

    fun shareZone(guid:String,zoneId:Int): LiveData<Resource<String>> {
        return object : NetworkBoundResource<String>(appExecutors) {
            override fun saveCallResult(item: String?) {

            }

            override fun shouldFetch(data: String?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<String> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<String>> {
                val deleteZoneVO=DeleteVO(guid.md5(),zoneId,System.currentTimeMillis())
                deleteZoneVO.signature= beanToJson(deleteZoneVO).sha256()
                return apiService.shareZone(deleteZoneVO)
            }
        }.asLiveData()
    }

    fun joinZone(guid:String,invitationCode:String): LiveData<Resource<ZoneDetail>> {
        return object : NetworkBoundResource<ZoneDetail>(appExecutors) {
            override fun saveCallResult(item: ZoneDetail?) {
                item?.let {
                    zoneDao.resetAllActiveZone()
                    zoneDao.insert(Zone(it.id,it.name,it.netWorkKey,it.nextDeviceIndex,it.nextGroupIndex,it.active))
                    singleDeviceDao.deleteAll(it.id)
                    roomDao.deleteAll(it.id)
                    it.devices?.let {it2 -> singleDeviceDao.insertAll(it2)}
                    it.groups?.let { it1 -> roomDao.insertAll(it1)}
                }
            }

            override fun shouldFetch(data: ZoneDetail?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<ZoneDetail> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<ZoneDetail>> {
                val baseVo=JoinZoneVO(invitationCode,guid.md5(),System.currentTimeMillis())
                baseVo.signature= beanToJson(baseVo).sha256()
                return apiService.joinZone(baseVo)
            }
        }.asLiveData()
    }


    fun getCurrentZone(guid:String): LiveData<Resource<ZoneDetail>> {
        return object : NetworkBoundResource<ZoneDetail>(appExecutors) {
            override fun saveCallResult(item: ZoneDetail?) {
                item?.let {
                    zoneDao.resetAllActiveZone()
                    zoneDao.insert(Zone(it.id,it.name,it.netWorkKey,it.nextDeviceIndex,it.nextGroupIndex,it.active))
                    singleDeviceDao.deleteAll(it.id)
                    roomDao.deleteAll(it.id)
                    it.devices?.let {it2 -> singleDeviceDao.insertAll(it2)}
                    it.groups?.let { it1 -> roomDao.insertAll(it1)}
                }
            }

            override fun shouldFetch(data: ZoneDetail?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<ZoneDetail> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<ZoneDetail>> {
                val baseVo=BaseVO(guid.md5(),System.currentTimeMillis())
                baseVo.signature= beanToJson(baseVo).sha256()
                return apiService.getCurrentZone(baseVo)
            }
        }.asLiveData()
    }


    fun getZones(guid:String): LiveData<Resource<List<Zone>>> {
        return object : NetworkBoundResource<List<Zone>>(appExecutors) {
            override fun saveCallResult(item: List<Zone>?) {
                item?.let {
                    zoneDao.deleteAll()
                    zoneDao.insertAll(item)
                }
            }

            override fun shouldFetch(data: List<Zone>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Zone>> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<List<Zone>>> {
                val baseVo=BaseVO(guid.md5(),System.currentTimeMillis())
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



