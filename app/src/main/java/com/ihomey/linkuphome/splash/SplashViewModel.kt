package com.ihomey.linkuphome.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class SplashViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getRemoteCurrentZone(imei: String): LiveData<Resource<ZoneDetail>> {
        return zoneRepository.getCurrentZone(imei)
    }

    fun getCurrentZoneId(): LiveData<Resource<Int>> {
        return zoneRepository.getCurrentZoneId()
    }

}
