package com.ihomey.linkuphome.setting

import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class SettingViewModel : ViewModel() {
    @Inject
    lateinit var zoneRepository: ZoneRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

//    fun getCurrentZone(): LiveData<Resource<Zone>> {
//        return zoneRepository.getCurrentZone()
//    }
}
