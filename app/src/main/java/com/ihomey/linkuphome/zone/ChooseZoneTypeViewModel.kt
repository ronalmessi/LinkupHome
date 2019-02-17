package com.ihomey.linkuphome.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.SubZoneRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.SubZone
import com.ihomey.linkuphome.data.vo.Zone
import javax.inject.Inject

class ChooseZoneTypeViewModel : ViewModel() {


    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    @Inject
    lateinit var zoneRepository: ZoneRepository


    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getZones(): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones()
    }

    fun createSubZone(type: Int, name: String, parentId: Int) {
        subZoneRepository.insert(SubZone(name, type, parentId))
    }
}
