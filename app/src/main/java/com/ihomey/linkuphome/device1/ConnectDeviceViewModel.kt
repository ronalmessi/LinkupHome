package com.ihomey.linkuphome.device1

import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.SingleDevice
import javax.inject.Inject

class ConnectDeviceViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun addSingleDevice(setting: LampCategory, singleDevice: SingleDevice) {
        mDeviceRepository.addSingleDevice(setting, singleDevice)
    }
}
