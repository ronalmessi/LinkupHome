package com.ihomey.linkuphome.data.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.data.api.AbsentLiveData
import com.ihomey.linkuphome.data.api.ApiResult
import com.ihomey.linkuphome.data.api.ApiService
import com.ihomey.linkuphome.data.api.NetworkBoundResource
import com.ihomey.linkuphome.data.db.LocalStateDao
import com.ihomey.linkuphome.data.db.RoomDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.db.ZoneDao
import com.ihomey.linkuphome.data.entity.DeviceState
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class DeviceRepository @Inject constructor(private var apiService: ApiService, private val singleDeviceDao: SingleDeviceDao, private val localStateDao: LocalStateDao,private val roomDao: RoomDao, private val zoneDao: ZoneDao, private var appExecutors: AppExecutors) {

    fun saveDevice(guid: String, zoneId: Int, type: Int, name: String): LiveData<Resource<SingleDevice>> {
        return object : NetworkBoundResource<SingleDevice>(appExecutors) {
            override fun saveCallResult(item: SingleDevice?) {
                item?.let {
                    singleDeviceDao.insert(it)
                    zoneDao.updateNextDeviceIndex(it.instructId+1,zoneId)
                }
            }

            override fun shouldFetch(data: SingleDevice?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<SingleDevice> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<SingleDevice>> {
                val saveDeviceVO = SaveDeviceVO(guid.md5(), name,  zoneId,  System.currentTimeMillis(), type)
                saveDeviceVO.signature = beanToJson(saveDeviceVO).sha256()
                return apiService.saveDevice(saveDeviceVO)
            }
        }.asLiveData()
    }


    fun deleteDevice(guid:String,deviceId:Int): LiveData<Resource<Boolean>> {
        return object : NetworkBoundResource<Boolean>(appExecutors) {
            override fun saveCallResult(item: Boolean?) {

            }

            override fun shouldFetch(data: Boolean?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Boolean> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Boolean>> {
                val deleteZoneVO=DeleteVO(guid.md5(),deviceId,System.currentTimeMillis())
                deleteZoneVO.signature= beanToJson(deleteZoneVO).sha256()
                return apiService.deleteDevice(deleteZoneVO)
            }
        }.asLiveData()
    }


    fun changeDeviceName(guid:String,spaceId:Int,id:Int,type:Int,newName:String): LiveData<Resource<SingleDevice>> {
        return object : NetworkBoundResource<SingleDevice>(appExecutors) {
            override fun saveCallResult(item: SingleDevice?) {
                item?.let {  singleDeviceDao.insert(it) }
            }

            override fun shouldFetch(data: SingleDevice?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<SingleDevice> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<SingleDevice>> {
                val changeZoneNameVO=ChangeDeviceNameVO(guid.md5(),id,newName,spaceId,System.currentTimeMillis(),type)
                changeZoneNameVO.signature= beanToJson(changeZoneNameVO).sha256()
                return apiService.changeDeviceName(changeZoneNameVO)
            }
        }.asLiveData()
    }


    fun changeDeviceState(guid:String,id:Int,name:String,value:String): LiveData<Resource<SingleDevice>> {
        return object : NetworkBoundResource<SingleDevice>(appExecutors) {
            override fun saveCallResult(item: SingleDevice?) {
                item?.let {
                    singleDeviceDao.insert(it)
                    if (TextUtils.equals("on", name)&&it.roomId!=0){
                        val room=roomDao.getRoom(it.roomId)
                        val deviceList=singleDeviceDao.getDevices(it.zoneId, it.roomId)
                        if(deviceList.filter { it.parameters?.on==1 }.size % deviceList.size==0){
                            room.parameters?.on=if(deviceList.filter { it.parameters?.on == 1 }.isEmpty()) 0 else 1
                            room.parameters?.let { it1 -> roomDao.updateState(it.roomId, it1) }
                        }
                    }
                }
            }

            override fun shouldFetch(data: SingleDevice?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<SingleDevice> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<SingleDevice>> {
                val changeDeviceStateVO=ChangeDeviceStateVO(guid.md5(),id,name,value,System.currentTimeMillis())
                changeDeviceStateVO.signature= beanToJson(changeDeviceStateVO).sha256()
                return apiService.changeDeviceState(changeDeviceStateVO)
            }
        }.asLiveData()
    }


    fun getDevices(zoneId: Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getDevices(zoneId)
            }
        }.asLiveData()
    }

    fun getDevicesByType(zoneId: Int,type:Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getDevicesByType(zoneId,type)
            }
        }.asLiveData()
    }


    fun getPagingDevices(zoneId: Int): LiveData<PagedList<SingleDevice>> {
        return LivePagedListBuilder(singleDeviceDao.getPagingDevices(zoneId), /* page size */6).build()
    }

    fun getPagingUnBondedDevices(zoneId: Int): LiveData<PagedList<SingleDevice>> {
        return LivePagedListBuilder(singleDeviceDao.getPagingUnBondedDevices(zoneId), /* page size */6).build()
    }

    fun getPagingBondedDevices(zoneId: Int,roomId:Int): LiveData<PagedList<SingleDevice>> {
        return LivePagedListBuilder(singleDeviceDao.getPagingBondedDevices(zoneId,roomId), /* page size */6).build()
    }

    fun getLocalState(id: Int): LiveData<Resource<LocalState>> {
        return object : DbBoundResource<LocalState>(appExecutors) {
            override fun loadFromDb(): LiveData<LocalState> {
                return localStateDao.getLocalState(id)
            }
        }.asLiveData()
    }

    fun updateLocalState(localState: LocalState) {
        appExecutors.diskIO().execute {
            localStateDao.insert(localState)
        }
    }

    fun deleteDevice(deviceId: Int) {
        appExecutors.diskIO().execute {
            singleDeviceDao.delete(deviceId)
            localStateDao.delete(deviceId)
        }
    }
}



