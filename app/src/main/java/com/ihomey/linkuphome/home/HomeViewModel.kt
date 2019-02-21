package com.ihomey.linkuphome.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject

class HomeViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

//    fun getCurrentZone(): LiveData<Resource<Zone>> {
//        return zoneRepository.getCurrentZone()
//    }
//


}
