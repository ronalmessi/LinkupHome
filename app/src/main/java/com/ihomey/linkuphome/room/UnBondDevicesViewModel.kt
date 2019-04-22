package com.ihomey.linkuphome.room

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class UnBondDevicesViewModel : ViewModel() {

    val unBondedDevicesResult1: LiveData<PagedList<Device>>
    val isUnBondedDevicesListEmptyLiveData = MediatorLiveData<Boolean>()

    val mZoneId = MutableLiveData<Int>()


    @Inject
    lateinit var deviceRepository: DeviceRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
        unBondedDevicesResult1 = Transformations.switchMap(mZoneId) { input ->
            deviceRepository.getPagingUnBondedDevices(input)
        }
        isUnBondedDevicesListEmptyLiveData.addSource(unBondedDevicesResult1) {
            isUnBondedDevicesListEmptyLiveData.value = it?.size == 0
        }
    }

    fun setZoneId(id: Int?) {
        mZoneId.value=id
    }
}
