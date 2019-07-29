package com.ihomey.linkuphome.data.repository

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
import com.ihomey.linkuphome.data.db.DeviceDao
import com.ihomey.linkuphome.data.db.ZoneDao
import com.ihomey.linkuphome.data.entity.DeviceState
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.spp.BluetoothSPP
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class DeviceRepository @Inject constructor(private var apiService: ApiService, private val deviceDao: DeviceDao, private val localStateDao: LocalStateDao, private val roomDao: RoomDao, private val zoneDao: ZoneDao, private var appExecutors: AppExecutors) {

    fun saveDevice(guid: String, zoneId: Int, type: Int, name: String): LiveData<Resource<Device>> {
        return object : NetworkBoundResource<Device>(appExecutors) {
            override fun saveCallResult(item: Device?) {
                item?.let {
                    deviceDao.insert(it)
                    zoneDao.updateNextDeviceIndex(it.instructId+1,zoneId)
                }
            }

            override fun shouldFetch(data: Device?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Device> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Device>> {
                val saveDeviceVO = SaveDeviceVO(guid.md5(), name,  zoneId,  System.currentTimeMillis(), type)
                saveDeviceVO.signature = beanToJson(saveDeviceVO).sha256()
                return apiService.saveDevice(saveDeviceVO)
            }
        }.asLiveData()
    }

    fun deleteDevice(guid:String,deviceId:String): LiveData<Resource<Boolean>> {
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


    fun changeDeviceName(guid:String,spaceId:Int,id:String,type:Int,newName:String): LiveData<Resource<Device>> {
        return object : NetworkBoundResource<Device>(appExecutors) {
            override fun saveCallResult(item: Device?) {
                item?.let {  deviceDao.insert(it) }
            }

            override fun shouldFetch(data: Device?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Device> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Device>> {
                val changeZoneNameVO=ChangeDeviceNameVO(guid.md5(),id,newName,spaceId,System.currentTimeMillis(),type)
                changeZoneNameVO.signature= beanToJson(changeZoneNameVO).sha256()
                return apiService.changeDeviceName(changeZoneNameVO)
            }
        }.asLiveData()
    }


    fun changeDeviceState(guid:String,id:String,name:String,value:String): LiveData<Resource<Device>> {
        return object : NetworkBoundResource<Device>(appExecutors) {
            override fun saveCallResult(item: Device?) {

            }

            override fun shouldFetch(data: Device?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Device> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Device>> {
                val changeDeviceStateVO=ChangeDeviceStateVO(guid.md5(),id,name,value,System.currentTimeMillis())
                changeDeviceStateVO.signature= beanToJson(changeDeviceStateVO).sha256()
                return apiService.changeDeviceState(changeDeviceStateVO)
            }
        }.asLiveData()
    }


    fun getDevices(zoneId: Int): LiveData<Resource<List<Device>>> {
        return object : DbBoundResource<List<Device>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Device>> {
                return deviceDao.getDevices(zoneId)
            }
        }.asLiveData()
    }

    fun getDevicesByType(zoneId: Int,type:Int): LiveData<Resource<List<Device>>> {
        return object : DbBoundResource<List<Device>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Device>> {
                return deviceDao.getDevicesByType(zoneId,type)
            }
        }.asLiveData()
    }


    fun getPagingDevices(zoneId: Int): LiveData<PagedList<Device>> {
        return LivePagedListBuilder(deviceDao.getPagingDevices(zoneId), /* page size */6).build()
    }

    fun getPagingUnBondedDevices(zoneId: Int): LiveData<PagedList<Device>> {
        return LivePagedListBuilder(deviceDao.getPagingUnBondedDevices(zoneId), /* page size */6).build()
    }

    fun getPagingBondedDevices(zoneId: Int,roomId:Int): LiveData<PagedList<Device>> {
        return LivePagedListBuilder(deviceDao.getPagingBondedDevices(zoneId,roomId), /* page size */6).build()
    }

    fun getLocalState(id: String): LiveData<Resource<LocalState>> {
        return object : DbBoundResource<LocalState>(appExecutors) {
            override fun loadFromDb(): LiveData<LocalState> {
                return localStateDao.getLocalState(id)
            }
        }.asLiveData()
    }

    fun saveDevice(device: Device) {
        appExecutors.diskIO().execute {
            deviceDao.insert(device)
        }
    }

    fun updateLocalState(localState: LocalState) {
        appExecutors.diskIO().execute {
            localStateDao.insert(localState)
        }
    }

    fun updateState(device: Device, deviceState:DeviceState) {
        appExecutors.diskIO().execute {
            deviceDao.updateState(device.id,deviceState)
        }
    }

    fun updateRoomAndDeviceState(device: Device, deviceState:DeviceState) {
        appExecutors.diskIO().execute {
            deviceDao.updateState(device.id,deviceState)
            val room=roomDao.getRoom(device.roomId)
            if(room!=null){
                val deviceList=deviceDao.getDevices(device.zoneId, device.roomId)
                if(deviceList.filter { it.parameters?.on==1 }.size % deviceList.size==0){
                    room.parameters?.on=if(deviceList.filter { it.parameters?.on == 1 }.isEmpty()) 0 else 1
                    room.parameters?.let { it1 -> roomDao.updateState(device.roomId, it1) }
                }
            }
        }
    }

    fun deleteDevice(deviceId: String) {
        appExecutors.diskIO().execute {
            deviceDao.delete(deviceId)
            localStateDao.delete(deviceId)
        }
    }
}



