package com.ihomey.linkuphome.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject

class HomeActivityViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var mCategoryRepository: CategoryRepository

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    @Inject
    lateinit var modelRepository: Model1Repository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    private val mCurrentZoneId = MutableLiveData<Int>()

    val devicesResult: LiveData<Resource<List<SingleDevice>>>
    val roomsResult: LiveData<Resource<List<Room>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
        devicesResult = Transformations.switchMap(mCurrentZoneId) { input ->
            mDeviceRepository.getDevices(input)
        }
        roomsResult = Transformations.switchMap(mCurrentZoneId) { input ->
            subZoneRepository.getRooms(input)
        }
    }


    fun getZones(): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones()
    }

    fun getCurrentZone(): LiveData<Resource<ZoneSetting>> {
        return zoneRepository.getCurrentZone()
    }



    fun setCurrentZoneId(zoneId: Int) {
        mCurrentZoneId.value = zoneId
    }


    private val bridgeState = MutableLiveData<Boolean>()

    private var currentControlDevice=MutableLiveData<SingleDevice>()


    fun getBridgeState(): MutableLiveData<Boolean> {
        return bridgeState
    }

    fun setBridgeState(connected: Boolean) {
        bridgeState.value = connected
    }

    fun getGlobalSetting(): LiveData<Resource<LampCategory>>? {
        return mCategoryRepository.getGlobalSetting()
    }

    fun getCurrentControlDevice(): MutableLiveData<SingleDevice> {
        return currentControlDevice
    }

    fun setCurrentControlDevice(singleDevice: SingleDevice) {
        currentControlDevice.value = singleDevice
    }



    fun getModels(deviceId: Int,zoneId: Int):LiveData<Resource<List<Model>>> {
        return modelRepository.getModels(deviceId,zoneId)
    }


    fun setCurrentZone(id: Int) {
        zoneRepository.setCurrentZone(id)
    }



    //room

     val mSelectedRoom = MutableLiveData<Room>()

    fun addRoom(currentSetting: Setting, type: Int, name: String) {
        subZoneRepository.addRoom(currentSetting,name, type)
    }

    fun deleteRoom(id: Int) {
        subZoneRepository.delete(id)
    }

    fun updateRoom(room: Room) {
        subZoneRepository.updateSubZoneState(room.id, room.state)
    }

    fun setSelectedRoom(room:Room) {
        mSelectedRoom.value=room
    }

    fun updateSendTypes(roomId: Int,zoneId:Int) {
        subZoneRepository.updateSendTypes(roomId,zoneId)
    }

    fun updateRoomName(newName: String, id: Int) {
        subZoneRepository.updateRoomName(newName, id)
    }



    //device
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
}