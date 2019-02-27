package com.ihomey.linkuphome.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.SubZoneRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ZoneSettingViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    //zone

    fun getZones(): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones()
    }

    fun getDevices(zoneId:Int): LiveData<Resource<List<SingleDevice>>> {
        return  mDeviceRepository.getDevices(zoneId)
    }

    fun updateZoneName(newName: String, id: Int) {
        zoneRepository.updateZoneName(newName, id)
    }

    fun deleteZone(id: Int) {
        zoneRepository.deleteZone(id)
    }

}
