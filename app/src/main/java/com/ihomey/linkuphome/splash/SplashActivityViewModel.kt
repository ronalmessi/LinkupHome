package com.ihomey.linkuphome.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.SettingRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject

class SplashActivityViewModel : ViewModel() {

    @Inject
    lateinit var settingRepository: SettingRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun getSetting(): LiveData<Resource<Setting>> {
        return settingRepository.getSetting()
    }
}
