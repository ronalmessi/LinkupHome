package com.ihomey.linkuphome.room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.Model1Repository
import com.ihomey.linkuphome.data.repository.RoomRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class UnBindedDevicesViewModel : ViewModel() {

    @Inject
    lateinit var deviceRepository: DeviceRepository

    @Inject
    lateinit var modelRepository: Model1Repository

    @Inject
    lateinit var roomRepository: RoomRepository

//    val devicesResult: LiveData<Resource<List<SingleDevice>>>

    val mSubZoneId = MutableLiveData<Int>()

    init {
        DaggerAppComponent.builder().build().inject(this)
//        devicesResult = Transformations.switchMap(mSubZoneId) { input ->
//            deviceRepository.getUnBondedDevices(input)
//        }
    }

    fun setSubZoneId(id: Int) {
        mSubZoneId.value=id
    }

//    fun createModel(model: Model1) {
////        modelRepository.addModel(model)
//    }


}
