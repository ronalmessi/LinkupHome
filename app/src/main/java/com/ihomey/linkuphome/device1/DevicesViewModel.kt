package com.ihomey.linkuphome.device1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class DevicesViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    private val mCurrentZoneId = MutableLiveData<Int>()

//    val devicesResult: LiveData<Resource<List<Device>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
//        devicesResult = Transformations.switchMap(mCurrentZoneId) { input ->
//            mDeviceRepository.getDevices(input)
//        }
    }

//    fun setCurrentZoneId(zoneId: Int) {
//        mCurrentZoneId.value = zoneId
//    }

    fun deleteSingleDevice(singleDeviceId: Int) {
//        mDeviceRepository.deleteSingleDevice(0, singleDeviceId)
    }
}
