package com.ihomey.linkuphome.device1

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ConnectDeviceViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

//    fun addSingleDevice(currentSetting: Setting, singleDevice: SingleDevice) {
//        mDeviceRepository.addSingleDevice(currentSetting, singleDevice)
//    }

    fun saveDevice(guid:String,zoneId:Int,type:Int,name:String): LiveData<Resource<SingleDevice>> {
        return mDeviceRepository.saveDevice(guid, zoneId, type, name)
    }
}
