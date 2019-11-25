package com.ihomey.linkuphome.devicecontrol.scene

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class SceneSettingViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    private val mCurrentDeviceId = MutableLiveData<String>()
    val mCurrentLocalState: LiveData<Resource<LocalState>>


    init {
        DaggerAppComponent.builder().build().inject(this)
        mCurrentLocalState = Transformations.switchMap(mCurrentDeviceId) { input ->
            mDeviceRepository.getLocalState(input)
        }
    }

    fun setCurrentDeviceId(deviceId: String?) {
        mCurrentDeviceId.value = deviceId
    }

    fun updateLocalState(localState: LocalState) {
        mDeviceRepository.updateLocalState(localState)
    }
}
