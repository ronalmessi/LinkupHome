package com.ihomey.linkuphome.share1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.SubZoneRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject

class ShareZoneViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    private val mCurrentZone = MutableLiveData<Zone>()


    init {
        DaggerAppComponent.builder().build().inject(this)
//        subZonesResult = Transformations.switchMap(mCurrentZone) { input ->
//            subZoneRepository.getSubZoneModels(input.id)
//        }
    }

    fun setCurrentZone(zone: Zone) {
        mCurrentZone.value = zone
    }

}
