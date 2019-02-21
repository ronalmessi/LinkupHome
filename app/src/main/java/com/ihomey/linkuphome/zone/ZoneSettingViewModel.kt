package com.ihomey.linkuphome.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.SubZoneRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject

class ZoneSettingViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getZones(): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones()
    }

    fun updateZoneName(newName: String, id: Int) {
        zoneRepository.updateZoneName(newName, id)
    }

    fun deleteZone(id: Int) {
        zoneRepository.deleteZone(id)
    }

}
