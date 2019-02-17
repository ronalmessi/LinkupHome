package com.ihomey.linkuphome.zone

import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent

import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Zone
import javax.inject.Inject

class CreateZoneViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun createZone(name: String, isCurrent: Boolean) {
        zoneRepository.insert(Zone(name,isCurrent))
    }

}
