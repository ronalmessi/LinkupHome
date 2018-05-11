package com.ihomey.linkuphome.time

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.iclass.soocsecretary.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.GroupRepository
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.DeviceInfo
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject

class TimerSettingViewModel : ViewModel() {

    private var currentControlDevice: LiveData<Resource<ControlDevice>>

    private var currentDeviceInfo = MutableLiveData<DeviceInfo>()

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    @Inject
    lateinit var mGroupRepository: GroupRepository

    init {
        DaggerAppComponent.builder().build().inject(this)

        currentControlDevice = Transformations.switchMap(currentDeviceInfo, { input ->
            if (input.deviceId in 1..32768) {
                mGroupRepository.getGroup(input.deviceType, input.deviceId)
            } else {
                mDeviceRepository.getDevice(input.deviceType, input.deviceId)
            }
        })
    }

    fun getCurrentControlDevice(): LiveData<Resource<ControlDevice>> {
        return currentControlDevice
    }

    fun setCurrentControlDeviceInfo(deviceInfo: DeviceInfo) {
        currentDeviceInfo.value = deviceInfo
    }

    fun updateDevice(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            if (controlDevice.id in 1..32768) {
                mGroupRepository.updateDevice(GroupDevice(controlDevice.id, controlDevice.device, controlDevice.state))
            } else {
                mDeviceRepository.updateDeviceState(controlDevice.id, controlDevice.state)
            }
        }

    }
}