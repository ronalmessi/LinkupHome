package com.ihomey.linkuphome.device1

import android.net.MacAddress
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Device
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
            mDeviceRepository.getDevicesByType(input.zoneId,input.type)
        }
    }

    fun saveDevice(guid:String,zoneId:Int,type:Int,name:String): LiveData<Resource<Device>> {
        return mDeviceRepository.saveDevice(guid, zoneId, type, name)
    }

    fun saveDevice(guid:String,zoneId:Int,type:Int,name:String,macAddress: String): LiveData<Resource<Device>> {
        return mDeviceRepository.saveDevice(guid, zoneId, type, name,macAddress)
    }


    fun setQuery(zoneId: Int,type:Int){
        mQuery.value=Query(zoneId,type)
    }

}
