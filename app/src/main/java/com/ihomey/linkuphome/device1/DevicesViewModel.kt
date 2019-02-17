package com.ihomey.linkuphome.device1

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.SingleDevice
import javax.inject.Inject

class DevicesViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getDevices(): LiveData<Resource<List<SingleDevice>>> {
        return mDeviceRepository.getDevices()
    }

    fun deleteSingleDevice(singleDeviceId: Int) {
        mDeviceRepository.deleteSingleDevice(0, singleDeviceId)
    }
}
