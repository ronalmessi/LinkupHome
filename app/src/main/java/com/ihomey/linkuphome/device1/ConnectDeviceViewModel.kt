package com.ihomey.linkuphome.device1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.DeviceState
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.vo.Query
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ConnectDeviceViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository


    private val mQuery = MutableLiveData<Query>()

    val devicesResult: LiveData<Resource<List<Device>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
        devicesResult = Transformations.switchMap(mQuery) { input ->
            mDeviceRepository.getDevicesByType(input.zoneId, input.type)
        }
    }

    fun saveDevice(guid: String, zoneId: Int, type: Int, name: String,pid: Int,meshInfo:String?): LiveData<Resource<Device>> {
        return mDeviceRepository.saveDevice(guid, zoneId, type,name,pid,meshInfo)
    }

    fun saveDevice(type: Int, zoneId: Int, name: String, macAddress: String) {
        return mDeviceRepository.saveDevice(Device(macAddress, zoneId, 0, name, type, 0,0, DeviceState()))
    }



    fun saveDevice(type: Int, zoneId: Int, name: String, macAddress: String, instructId: Int) {
        return mDeviceRepository.saveDevice(Device(macAddress, zoneId, 0, name, type, instructId, 0,DeviceState()))
    }

    fun setQuery(zoneId: Int, type: Int) {
        mQuery.value = Query(zoneId, type)
    }

}
