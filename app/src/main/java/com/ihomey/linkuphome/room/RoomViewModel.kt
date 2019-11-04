package com.ihomey.linkuphome.room

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class RoomViewModel : ViewModel() {

    @Inject
    lateinit var deviceRepository: DeviceRepository


    val bondedDevicesResult1: LiveData<PagedList<Device>>

    val isBondedDevicesListEmptyLiveData = MediatorLiveData<Boolean>()


    val mCurrentRoom = MutableLiveData<Room>()

    init {
        DaggerAppComponent.builder().build().inject(this)

        bondedDevicesResult1 = Transformations.switchMap(mCurrentRoom) { input ->
            deviceRepository.getPagingBondedDevices(input.zoneId, input.id)
        }
        isBondedDevicesListEmptyLiveData.addSource(bondedDevicesResult1) {
            isBondedDevicesListEmptyLiveData.value = it?.size == 0
        }
    }

    fun setCurrentRoom(room: Room?) {
        mCurrentRoom.value = room
    }
}
