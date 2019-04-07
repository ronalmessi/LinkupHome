package com.ihomey.linkuphome.data.repository

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.beanToJson
import com.ihomey.linkuphome.data.api.AbsentLiveData
import com.ihomey.linkuphome.data.api.ApiResult
import com.ihomey.linkuphome.data.api.ApiService
import com.ihomey.linkuphome.data.api.NetworkBoundResource
import com.ihomey.linkuphome.data.db.RoomDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.vo.*

import com.ihomey.linkuphome.md5
import com.ihomey.linkuphome.sha256

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class RoomRepository @Inject constructor(private var apiService: ApiService, private val roomDao: RoomDao, private val singleDeviceDao: SingleDeviceDao, private var appExecutors: AppExecutors) {

    fun saveRoom(guid: String, zoneId: Int, type: Int, name: String): LiveData<Resource<Room>> {
        return object : NetworkBoundResource<Room>(appExecutors) {
            override fun saveCallResult(item: Room?) {
                item?.let { roomDao.insert(it) }
            }

            override fun shouldFetch(data: Room?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Room> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Room>> {
                val saveRoomVO = SaveRoomVO(guid.md5(), name, zoneId, System.currentTimeMillis(), type)
                saveRoomVO.signature = beanToJson(saveRoomVO).sha256()
                return apiService.saveRoom(saveRoomVO)
            }
        }.asLiveData()
    }

    fun deleteRoom(guid: String, roomId: Int): LiveData<Resource<Boolean>> {
        return object : NetworkBoundResource<Boolean>(appExecutors) {
            override fun saveCallResult(item: Boolean?) {
                roomDao.delete(roomId)
                singleDeviceDao.unBondFromRoom(roomId)
            }

            override fun shouldFetch(data: Boolean?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Boolean> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Boolean>> {
                val deleteZoneVO = DeleteVO(guid.md5(), roomId, System.currentTimeMillis())
                deleteZoneVO.signature = beanToJson(deleteZoneVO).sha256()
                return apiService.deleteRoom(deleteZoneVO)
            }
        }.asLiveData()
    }

    fun changeRoomName(guid: String, spaceId: Int, id: Int, type: Int, newName: String): LiveData<Resource<Room>> {
        return object : NetworkBoundResource<Room>(appExecutors) {
            override fun saveCallResult(item: Room?) {
                item?.let { roomDao.insert(it) }
            }

            override fun shouldFetch(data: Room?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Room> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Room>> {
                val changeZoneNameVO = ChangeDeviceNameVO(guid.md5(), id, newName, spaceId, System.currentTimeMillis(), type)
                changeZoneNameVO.signature = beanToJson(changeZoneNameVO).sha256()
                return apiService.changeRoomName(changeZoneNameVO)
            }
        }.asLiveData()
    }

    fun bindDevice(guid: String, spaceId: Int, groupInstructId: Int, deviceInstructId: Int, act: String): LiveData<Resource<Room>> {
        return object : NetworkBoundResource<Room>(appExecutors) {
            override fun saveCallResult(item: Room?) {
                item?.let {
                    roomDao.insert(item)
                    if (TextUtils.equals("add", act)) {
                        singleDeviceDao.bondToRoom(item.id, deviceInstructId, spaceId)
                    } else {
                        singleDeviceDao.unBondDeviceFromRoom(deviceInstructId, spaceId)
                    }
                }
            }

            override fun shouldFetch(data: Room?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Room> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Room>> {
                val bindDeviceVO = BindDeviceVO(guid.md5(), spaceId, groupInstructId, deviceInstructId, act, System.currentTimeMillis())
                bindDeviceVO.signature = beanToJson(bindDeviceVO).sha256()
                return apiService.bindDevice(bindDeviceVO)
            }
        }.asLiveData()
    }

    fun changeRoomState(guid: String, id: Int, name: String, value: String): LiveData<Resource<Room>> {
        return object : NetworkBoundResource<Room>(appExecutors) {
            override fun saveCallResult(item: Room?) {
                item?.let {
                    roomDao.insert(it)
                    for (device in singleDeviceDao.getDevices(it.zoneId, it.id)) {
                        val deviceState = device.parameters
                        if (TextUtils.equals("on", name)) deviceState?.on = value.toInt() else deviceState?.brightness = value.toInt()
                        deviceState?.let { it1 -> singleDeviceDao.updateState(it.id, it1) }
                    }
                }
            }

            override fun shouldFetch(data: Room?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Room> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<Room>> {
                val changeDeviceStateVO = ChangeDeviceStateVO(guid.md5(), id, name, value, System.currentTimeMillis())
                changeDeviceStateVO.signature = beanToJson(changeDeviceStateVO).sha256()
                return apiService.changeRoomState(changeDeviceStateVO)
            }
        }.asLiveData()
    }

    fun getRooms(zoneId: Int): LiveData<Resource<List<RoomAndDevices>>> {
        return object : DbBoundResource<List<RoomAndDevices>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<RoomAndDevices>> {
                return roomDao.getRooms(zoneId)
            }
        }.asLiveData()
    }
}



