package com.ihomey.linkuphome.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.entity.Model
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject

class RoomViewModel : ViewModel() {

    @Inject
    lateinit var deviceRepository: DeviceRepository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    @Inject
    lateinit var modelRepository: Model1Repository

    val bindedDevicesResult: LiveData<Resource<List<SingleDevice>>>

    val unBindedDevicesResult: LiveData<Resource<List<SingleDevice>>>

    val mCurrentRoom = MutableLiveData<Room>()

    init {
        DaggerAppComponent.builder().build().inject(this)
        bindedDevicesResult = Transformations.switchMap(mCurrentRoom) { input ->
            deviceRepository.getBindedDevices(input.zoneId, input.id)
        }
        unBindedDevicesResult = Transformations.switchMap(mCurrentRoom) { input ->
            deviceRepository.getUnBindedDevices(input.zoneId, input.id)
        }
    }

    fun setRoom(room: Room) {
        mCurrentRoom.value = room
    }

    fun updateDevice(singleDevice: SingleDevice) {
        deviceRepository.updateDeviceState(singleDevice.id, singleDevice.state)
    }

    fun deleteModel(deviceId: Int, roomId: Int, zoneId: Int) {
        modelRepository.deleteModel(deviceId, roomId, zoneId)
    }


    fun createModel(model: Model) {
        modelRepository.addModel(model)
    }


}
