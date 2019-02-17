package com.ihomey.linkuphome.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.CategoryRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Zone
import javax.inject.Inject

class HomeActivityViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var mCategoryRepository: CategoryRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }


    private val bridgeState = MutableLiveData<Boolean>()

    fun getCurrentZone(): LiveData<Resource<Zone>> {
        return zoneRepository.getCurrentZone()
    }

    fun getBridgeState(): MutableLiveData<Boolean> {
        return bridgeState
    }

    fun setBridgeState(connected: Boolean) {
        bridgeState.value = connected
    }

    fun getGlobalSetting(): LiveData<Resource<LampCategory>>? {
        return mCategoryRepository.getGlobalSetting()
    }
}
