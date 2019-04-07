package com.ihomey.linkuphome.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ZoneSettingViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository



    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getLocalZones(): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones()
    }

    fun getRemoteZones(guid:String): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones(guid)
    }

    fun deleteZone(guid:String,zoneId:Int): LiveData<Resource<Int>> {
        return zoneRepository.deleteZone(guid,zoneId)
    }

    fun switchZone(guid:String,zoneId:Int): LiveData<Resource<ZoneDetail>> {
        return zoneRepository.switchZone(guid,zoneId)
    }

    fun getZone(guid:String,zoneId:Int): LiveData<Resource<ZoneDetail>> {
        return zoneRepository.getZone(guid,zoneId)
    }

    fun changeZoneName(guid:String,zoneId:Int,newName:String): LiveData<Resource<Zone>> {
        return zoneRepository.changeZoneName(guid,zoneId,newName)
    }

    fun getDevices(zoneId:Int): LiveData<Resource<List<SingleDevice>>> {
        return mDeviceRepository.getDevices(zoneId)
    }

}
