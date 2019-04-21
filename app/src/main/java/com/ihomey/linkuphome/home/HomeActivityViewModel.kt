package com.ihomey.linkuphome.home

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.RemoveDeviceVo
import com.ihomey.linkuphome.data.vo.Resource
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

    val devicesResult: LiveData<PagedList<SingleDevice>>
    val roomsResult:  LiveData<PagedList<RoomAndDevices>>


    val isDeviceListEmptyLiveData = MediatorLiveData<Boolean>()
    val isRoomListEmptyLiveData = MediatorLiveData<Boolean>()

    init {
        DaggerAppComponent.builder().build().inject(this)
        mCurrentZone = Transformations.switchMap(mCurrentZoneId) { input ->
            zoneRepository.getZone(input)
        }
        devicesResult = Transformations.switchMap(mCurrentZoneId) { input ->
            mDeviceRepository.getPagingDevices(input)
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

    //bridge
    private val bridgeState = MutableLiveData<Boolean>()

    fun getBridgeState(): MutableLiveData<Boolean> {
        return bridgeState
    }

    fun setBridgeState(connected: Boolean) {
        bridgeState.value = connected
    }

    //remove device
    val mRemoveDeviceVo = MutableLiveData<RemoveDeviceVo>()

    fun setRemoveDeviceVo(removeDeviceVo: RemoveDeviceVo?) {
        mRemoveDeviceVo.value = removeDeviceVo
    }


    //removeDeviceFlag
    private val removeDeviceFlag = MutableLiveData<Boolean>()

    fun getRemoveDeviceFlag(): MutableLiveData<Boolean> {
        return removeDeviceFlag
    }

    fun setRemoveDeviceFlag(flag: Boolean) {
        removeDeviceFlag.value = flag
    }

    private var scanedDevice = MutableLiveData<SingleDevice>()

    fun getScanDevice(): MutableLiveData<SingleDevice> {
        return scanedDevice
    }

    fun setScanDevice(singleDevice: SingleDevice) {
        if (scanedDevice.value == null) scanedDevice.value = singleDevice
    }

    fun clearScanedDevice() {
        scanedDevice.value = null
    }

    fun setCurrentZoneId(zoneId: Int?) {
        mCurrentZoneId.value = zoneId
    }

    val mSelectedRoom = MutableLiveData<RoomAndDevices>()


    fun saveRoom(guid:String,zoneId:Int,type:Int,name:String): LiveData<Resource<Room>> {
        return roomRepository.saveRoom(guid, zoneId, type, name)
    }

    fun deleteRoom(guid:String,roomId: Int): LiveData<Resource<Boolean>> {
        return roomRepository.deleteRoom(guid, roomId)
    }

    fun changeRoomName(guid:String,spaceId:Int,id:Int,type:Int,newName:String): LiveData<Resource<Room>> {
        return roomRepository.changeRoomName(guid,spaceId,id,type,newName)
    }

    fun changeRoomState(guid:String,id:Int,name:String,value:String): LiveData<Resource<Room>> {
        return roomRepository.changeRoomState(guid,id,name,value)
    }

    fun setSelectedRoom(roomAndDevices: RoomAndDevices) {
        mSelectedRoom.value = roomAndDevices
    }


    //device
    private var currentControlDevice = MutableLiveData<SingleDevice>()

    fun getCurrentControlDevice(): MutableLiveData<SingleDevice> {
        return currentControlDevice
    }

    fun setCurrentControlDevice(singleDevice: SingleDevice) {
        currentControlDevice.value = singleDevice
    }

    fun deleteDevice(deviceId: Int) {
        mRemoveDeviceVo.value=null
        return mDeviceRepository.deleteDevice(deviceId)
    }

    fun deleteDevice(guid:String,deviceId: Int): LiveData<Resource<Boolean>> {
        return mDeviceRepository.deleteDevice(guid, deviceId)
    }

    fun changeDeviceName(guid:String,spaceId:Int,id:Int,type:Int,newName:String): LiveData<Resource<SingleDevice>> {
        return mDeviceRepository.changeDeviceName(guid,spaceId,id,type,newName)
    }

    fun changeDeviceState(guid:String,id:Int,name:String,value:String): LiveData<Resource<SingleDevice>> {
        return mDeviceRepository.changeDeviceState(guid,id,name,value)
    }


    fun bindDevice(guid: String,spaceId: Int,groupInstructId: Int, deviceInstructId: String,act: String): LiveData<Resource<Room>> {
        return roomRepository.bindDevice(guid,spaceId,groupInstructId,deviceInstructId,act)
    }


    fun updateState(roomAndDevices: RoomAndDevices,deviceState:DeviceState) {
        return roomRepository.updateState(roomAndDevices, deviceState)
    }

    fun updateDeviceState(singleDevice:SingleDevice,deviceState:DeviceState) {
        return mDeviceRepository.updateState(singleDevice, deviceState)
    }

    fun updateRoomAndDeviceState(singleDevice:SingleDevice,deviceState:DeviceState) {
        return mDeviceRepository.updateRoomAndDeviceState(singleDevice, deviceState)
    }


}
