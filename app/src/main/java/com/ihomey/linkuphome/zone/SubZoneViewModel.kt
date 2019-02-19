package com.ihomey.linkuphome.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject

class SubZoneViewModel : ViewModel() {

    @Inject
    lateinit var deviceRepository: DeviceRepository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    @Inject
    lateinit var modelRepository: Model1Repository

    val devicesResult: LiveData<Resource<List<SingleDevice>>>

    val mSubZoneId = MutableLiveData<Int>()

    init {
        DaggerAppComponent.builder().build().inject(this)
        devicesResult = Transformations.switchMap(mSubZoneId) { input ->
            deviceRepository.getBindedDevices(input)
        }
    }

    fun setSubZoneId(id: Int) {
        mSubZoneId.value=id
    }

    fun updateDevice(singleDevice: SingleDevice) {
        singleDevice.state?.let { deviceRepository.updateDeviceState(singleDevice.id, it) }
    }

    fun deleteModel(deviceId: Int, groupId: Int, groupIndex: Int) {
        modelRepository.deleteModel(deviceId, groupId, groupIndex)
    }

    fun updateSubZoneName(newName: String, id: Int) {
        subZoneRepository.updateSubZoneName(newName, id)
    }


}
