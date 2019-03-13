package com.ihomey.linkuphome.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.SettingRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class SplashViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getRemoteCurrentZone(imei:String): LiveData<Resource<Zone>> {
        return zoneRepository.getCurrentZone(imei)
    }

    fun getLocalCurrentZone():LiveData<Resource<Int>> {
        return zoneRepository.getCurrentZoneId()
    }
}
