package com.ihomey.linkuphome.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ShareZoneListViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getLocalZones(): LiveData<Resource<List<Zone>>> {
        return zoneRepository.getZones()
    }

    fun shareZone(guid:String,zoneId:Int): LiveData<Resource<String>> {
        return zoneRepository.shareZone(guid,zoneId)
    }

}
