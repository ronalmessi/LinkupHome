package com.ihomey.linkuphome.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class RoomViewModel : ViewModel() {

    @Inject
    lateinit var deviceRepository: DeviceRepository

//    val bondedDevicesResult: LiveData<Resource<List<SingleDevice>>>
//
//    val unBondedDevicesResult: LiveData<Resource<List<SingleDevice>>>

    val mCurrentRoom = MutableLiveData<Room>()

    init {
        DaggerAppComponent.builder().build().inject(this)
//        bondedDevicesResult = Transformations.switchMap(mCurrentRoom) { input ->
//            deviceRepository.getBondedDevices(input.zoneId,input.instructId)
//        }
//        unBondedDevicesResult = Transformations.switchMap(mCurrentRoom) { input ->
//            deviceRepository.getUnBondedDevices(input.zoneId)
//        }
    }

    fun setCurrentRoom(room:Room) {
        mCurrentRoom.value = room
    }

}
