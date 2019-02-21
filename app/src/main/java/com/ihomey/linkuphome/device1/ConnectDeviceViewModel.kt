package com.ihomey.linkuphome.device1

import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.entity.ZoneSetting
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.vo.LampCategory
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
