package com.ihomey.linkuphome.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.repository.SettingRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
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
