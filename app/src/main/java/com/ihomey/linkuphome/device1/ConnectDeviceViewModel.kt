package com.ihomey.linkuphome.device1

import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ConnectDeviceViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun addSingleDevice(currentSetting: Setting, singleDevice: SingleDevice) {
        mDeviceRepository.addSingleDevice(currentSetting, singleDevice)
    }
}
