package com.ihomey.linkuphome.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject

class HomeActivityViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var mCategoryRepository: CategoryRepository


    @Inject
    lateinit var mDeviceRepository: DeviceRepository


    @Inject
    lateinit var modelRepository: Model1Repository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getZones(): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones()
    }

    private val bridgeState = MutableLiveData<Boolean>()

    private var currentControlDevice=MutableLiveData<SingleDevice>()

//    fun getCurrentZone(): LiveData<Resource<Zone>> {
//        return zoneRepository.getCurrentZone()
//    }

    fun getBridgeState(): MutableLiveData<Boolean> {
        return bridgeState
    }

    fun setBridgeState(connected: Boolean) {
        bridgeState.value = connected
    }

    fun getGlobalSetting(): LiveData<Resource<LampCategory>>? {
        return mCategoryRepository.getGlobalSetting()
    }

    fun getCurrentControlDevice(): MutableLiveData<SingleDevice> {
        return currentControlDevice
    }

    fun setCurrentControlDevice(singleDevice: SingleDevice) {
        currentControlDevice.value = singleDevice
    }

    fun updateDevice(singleDevice: SingleDevice) {
        singleDevice.state?.let { mDeviceRepository.updateDeviceState(singleDevice.id, it) }
    }

    fun updateDeviceName(deviceId: Int, deviceName: String) {
            mDeviceRepository.updateSingleDeviceName(deviceId, deviceName)

    }

    fun getModels(deviceId: Int):LiveData<Resource<List<Model1>>> {
        return modelRepository.getModels(deviceId)
    }

    fun updateZoneName(newName: String, id: Int) {
        zoneRepository.updateZoneName(newName, id)
    }


    fun setCurrentZone(id: Int) {
        zoneRepository.setCurrentZone(id)
    }

    fun deleteZone(id: Int) {
        zoneRepository.deleteZone(id)
    }
}
