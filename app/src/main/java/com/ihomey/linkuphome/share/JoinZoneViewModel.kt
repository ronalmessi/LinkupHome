package com.ihomey.linkuphome.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class JoinZoneViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }


    fun joinZone(guid:String,invitationCode:String): LiveData<Resource<ZoneDetail>> {
        return zoneRepository.joinZone(guid,invitationCode)
    }

}
