package com.ihomey.linkuphome.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class CreateZoneViewModel : ViewModel() {


    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun createZone(imei: String, name: String): LiveData<Resource<Zone>> {
        return zoneRepository.createZone(imei, name)
    }

}
