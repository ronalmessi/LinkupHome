package com.ihomey.linkuphome.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.RegisterVO
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class HomeActivityViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var mSettingRepository: SettingRepository

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    @Inject
    lateinit var modelRepository: Model1Repository

    @Inject
    lateinit var roomRepository: RoomRepository

    private val mCurrentZoneId = MutableLiveData<Int>()
    val mCurrentZone: LiveData<Resource<Zone>>

    val devicesResult: LiveData<Resource<List<SingleDevice>>>
    val roomsResult: LiveData<Resource<List<Room>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
        mCurrentZone = Transformations.switchMap(mCurrentZoneId) { input ->
            zoneRepository.getZone(input)
        }
        devicesResult = Transformations.switchMap(mCurrentZoneId) { input ->
            mDeviceRepository.getDevices(input)
        }
        roomsResult = Transformations.switchMap(mCurrentZoneId) { input ->
            roomRepository.getRooms(input)
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

    private var scanedDevice = MutableLiveData<SingleDevice>()

    fun getScanedDevice(): MutableLiveData<SingleDevice> {
        return scanedDevice
    }

    fun setScanedDevice(singleDevice: SingleDevice) {
        if (scanedDevice.value == null) scanedDevice.value = singleDevice
    }

    fun clearScanedDevice() {
        scanedDevice.value = null
    }


    //model
    fun getModels(deviceId: Int, zoneId: Int): LiveData<Resource<List<Model>>> {
        return modelRepository.getModels(deviceId, zoneId)
    }


    //setting
    fun getGlobalSetting(): LiveData<Resource<Setting>> {
        return mSettingRepository.getSetting()
    }

    //zone
    fun setCurrentZoneId(zoneId: Int?) {
        mCurrentZoneId.value = zoneId
    }

    //room
    val mSelectedRoom = MutableLiveData<Room>()

    fun addRoom(currentSetting: Setting, currentZone: Zone, type: Int, name: String) {
        roomRepository.addRoom(currentSetting, currentZone, name, type)
    }

    fun deleteRoom(id: Int) {
        roomRepository.delete(id)
    }

    fun updateRoom(room: Room) {
        roomRepository.updateSubZoneState(room)
    }

    fun setSelectedRoom(room: Room) {
        mSelectedRoom.value = room
    }

    fun updateSendTypes(roomId: Int, zoneId: Int) {
        roomRepository.updateSendTypes(roomId, zoneId)
    }

    fun updateRoomName(newName: String, id: Int) {
        roomRepository.updateRoomName(newName, id)
    }


    //device

    private var currentControlDevice = MutableLiveData<SingleDevice>()

    fun getCurrentControlDevice(): MutableLiveData<SingleDevice> {
        return currentControlDevice
    }

    fun setCurrentControlDevice(singleDevice: SingleDevice) {
        currentControlDevice.value = singleDevice
    }

    fun addSingleDevice(currentSetting: Setting, singleDevice: SingleDevice) {
        mDeviceRepository.addSingleDevice(currentSetting, singleDevice)
    }

    fun deleteSingleDevice(singleDeviceId: Int) {
        mDeviceRepository.deleteSingleDevice(0, singleDeviceId)
    }

    fun updateDevice(singleDevice: SingleDevice) {
        mDeviceRepository.updateDeviceState(singleDevice.id, singleDevice.state)
    }

    fun updateDeviceName(deviceId: Int, deviceName: String) {
        mDeviceRepository.updateSingleDeviceName(deviceId, deviceName)

    }

    fun getDevices(deviceId: Int, type: Int): LiveData<Resource<List<SingleDevice>>> {
        return mDeviceRepository.getDevices(type, deviceId)

    }



}
