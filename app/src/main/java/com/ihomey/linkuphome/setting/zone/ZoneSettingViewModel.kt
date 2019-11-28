package com.ihomey.linkuphome.setting.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ZoneSettingViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var mDeviceRepository: DeviceRepository


    private val loadLocalZones = MutableLiveData<Boolean>()

    val zoneResult: LiveData<PagedList<Zone>>

    init {
        DaggerAppComponent.builder().build().inject(this)
        zoneResult = Transformations.switchMap(loadLocalZones) {
            zoneRepository.getPagingZones()
        }
    }

    fun getRemoteZones(guid: String): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones(guid)
    }

    fun loadLocalZones(){
        loadLocalZones.value=true
    }

    fun deleteZone(guid: String, zoneId: Int): LiveData<Resource<Int>> {
        return zoneRepository.deleteZone(guid, zoneId)
    }

    fun switchZone(guid: String, zoneId: Int): LiveData<Resource<ZoneDetail>> {
        return zoneRepository.switchZone(guid, zoneId)
    }

    fun getZone(guid: String, zoneId: Int): LiveData<Resource<ZoneDetail>> {
        return zoneRepository.getZone(guid, zoneId)
    }

    fun changeZoneName(guid: String, zoneId: Int, newName: String): LiveData<Resource<Zone>> {
        return zoneRepository.changeZoneName(guid, zoneId, newName)
    }

    fun shareZone(guid: String, zoneId: Int): LiveData<Resource<String>> {
        return zoneRepository.shareZone(guid, zoneId)
    }

}
