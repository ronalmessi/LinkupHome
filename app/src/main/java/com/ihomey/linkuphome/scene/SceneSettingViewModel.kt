package com.ihomey.linkuphome.scene

import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.GroupRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class SceneSettingViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    @Inject
    lateinit var mGroupRepository: GroupRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun updateDeviceSceneMode(deviceType:Int,deviceId: Int, sceneMode: Int) {
        if (deviceId in 1..32768) {
            mGroupRepository.updateDeviceSceneMode(deviceType,deviceId, sceneMode)
        } else {
            mDeviceRepository.updateDeviceSceneMode(deviceType,deviceId, sceneMode)
        }
    }

}