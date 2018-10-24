package com.ihomey.linkuphome.scene

import android.arch.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.GroupRepository
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
        if (deviceId in 1..32768&&deviceType<5) {
            mGroupRepository.updateDeviceSceneMode(deviceType,deviceId, sceneMode)
        } else {
            mDeviceRepository.updateDeviceSceneMode(deviceType,deviceId, sceneMode)
        }
    }

}