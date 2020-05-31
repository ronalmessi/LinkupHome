package com.ihomey.linkuphome.home

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.RoomRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.AppVersionInfo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class HomeActivityViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    @Inject
    lateinit var roomRepository: RoomRepository


    private val mCurrentZoneId = MutableLiveData<Int>()
    val mCurrentZone: LiveData<Resource<Zone>>

    val devicesResult: LiveData<PagedList<Device>>

    val allDevicesResult: LiveData<Resource<List<Device>>>

    val roomsResult: LiveData<PagedList<RoomAndDevices>>


    val isDeviceListEmptyLiveData = MediatorLiveData<Boolean>()
    val isRoomListEmptyLiveData = MediatorLiveData<Boolean>()

    private var currentControlDevice = MutableLiveData<Device>()

    init {
        DaggerAppComponent.builder().build().inject(this)
        mCurrentZone = Transformations.switchMap(mCurrentZoneId) { input ->
            zoneRepository.getZone(input)
        }
        devicesResult = Transformations.switchMap(mCurrentZoneId) { input ->
            mDeviceRepository.getPagingDevices(input)
        }
        allDevicesResult = Transformations.switchMap(mCurrentZoneId) { input ->
            mDeviceRepository.getDevices(input)
        }
        isDeviceListEmptyLiveData.addSource(devicesResult) {
            isDeviceListEmptyLiveData.value = it?.size == 0
        }
        roomsResult = Transformations.switchMap(mCurrentZoneId) { input ->
            roomRepository.getPagingRooms(input)
        }
        isRoomListEmptyLiveData.addSource(roomsResult) {
            isRoomListEmptyLiveData.value = it?.size == 0
        }
    }


    fun uploadMeshInfo(imei: String, id: Int?, zoneName: String?, meshInfo: String): LiveData<Resource<Zone>> {
        return zoneRepository.uploadMeshInfo(imei, id, zoneName, meshInfo)
    }


    //removeDeviceFlag
    private val removeDeviceFlag = MutableLiveData<Boolean>()

    fun getRemoveDeviceFlag(): MutableLiveData<Boolean> {
        return removeDeviceFlag
    }

    fun setRemoveDeviceFlag(flag: Boolean) {
        removeDeviceFlag.value = flag
    }

    private var scanedDevice = MutableLiveData<Device>()

    fun getScanDevice(): MutableLiveData<Device> {
        return scanedDevice
    }

    fun setScanDevice(device: Device?) {
        if (scanedDevice.value == null) scanedDevice.value = device
    }

    fun clearScanedDevice() {
        scanedDevice.value = null
    }

    fun setCurrentZoneId(zoneId: Int?) {
        mCurrentZoneId.value = zoneId
    }

    val mSelectedRoom = MutableLiveData<RoomAndDevices>()


    fun saveRoom(guid: String, zoneId: Int, type: Int, name: String): LiveData<Resource<Room>> {
        return roomRepository.saveRoom(guid, zoneId, type, name)
    }

    fun deleteRoom(guid: String, roomId: Int): LiveData<Resource<Boolean>> {
        return roomRepository.deleteRoom(guid, roomId)
    }

    fun changeRoomName(guid: String, spaceId: Int, id: Int, type: Int, newName: String): LiveData<Resource<Room>> {
        return roomRepository.changeRoomName(guid, spaceId, id, type, newName)
    }

    fun changeRoomState(guid: String, id: Int, name: String, value: String): LiveData<Resource<Room>> {
        return roomRepository.changeRoomState(guid, id, name, value)
    }

    fun setSelectedRoom(roomAndDevices: RoomAndDevices) {
        mSelectedRoom.value = roomAndDevices
    }

    fun getCurrentControlDevice(): MutableLiveData<Device> {
        return currentControlDevice
    }

    fun setCurrentControlDevice(device: Device) {
        currentControlDevice.value = device
    }

    fun deleteDevice(deviceId: String) {
        return mDeviceRepository.deleteDevice(deviceId)
    }

    fun deleteM1Device(deviceId: String) {
        return mDeviceRepository.deleteDevice(deviceId)
    }

    fun updateM1Version(deviceId: String, version: Int) {
        return mDeviceRepository.updateM1Version(deviceId, version)
    }

    fun deleteDevice(guid: String, deviceId: String): LiveData<Resource<Boolean>> {
        return mDeviceRepository.deleteDevice(guid, deviceId,null)
    }

    fun saveDevice(guid: String, zoneId: Int, type: Int, name: String,pid: Int,meshInfo:String?): LiveData<Resource<Device>> {
        return mDeviceRepository.saveDevice(guid, zoneId, type,name,pid,meshInfo)
    }

    fun changeDeviceName(guid: String, spaceId: Int, id: String,pid: Int, type: Int, newName: String): LiveData<Resource<Device>> {
        return mDeviceRepository.changeDeviceName(guid, spaceId, id,pid,type, newName)
    }

    fun updateDeviceName(device: Device, newName: String) {
        return mDeviceRepository.updateDeviceName(device, newName)
    }

    fun changeDeviceState(guid: String, id: String, name: String, value: String): LiveData<Resource<Device>> {
        return mDeviceRepository.changeDeviceState(guid, id, name, value)
    }


    fun bindDevice(guid: String, spaceId: Int, groupId: Int, deviceIds: String, act: String): LiveData<Resource<Room>> {
        return roomRepository.bindDevice(guid, spaceId, groupId, deviceIds, act)
    }


    fun updateState(roomAndDevices: RoomAndDevices, deviceState: DeviceState) {
        return roomRepository.updateState(roomAndDevices, deviceState)
    }

    fun updateDeviceState(device: Device, deviceState: DeviceState) {
        return mDeviceRepository.updateState(device, deviceState)
    }

    fun updateRoomAndDeviceState(device: Device, deviceState: DeviceState) {
        return mDeviceRepository.updateRoomAndDeviceState(device, deviceState)
    }


    //zone
    fun joinZone(guid: String, invitationCode: String): LiveData<Resource<ZoneDetail>> {
        return zoneRepository.joinZone(guid, invitationCode)
    }


    fun getAppVersionInfo(url:String): LiveData<Resource<AppVersionInfo>> {
        return zoneRepository.getAppVersionInfo(url)
    }
}
