package com.ihomey.linkuphome.zone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.SubZoneRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.SingleDevice
import com.ihomey.linkuphome.data.vo.SubZone
import com.ihomey.linkuphome.data.vo.Zone
import javax.inject.Inject

class ZonesViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    private val mCurrentZone = MutableLiveData<Zone>()

    val subZonesResult: LiveData<Resource<List<SubZone>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
        subZonesResult = Transformations.switchMap(mCurrentZone) { input ->
            subZoneRepository.getSubZones(input.id)
        }
    }

    fun getCurrentZone(): LiveData<Resource<Zone>> {
        return zoneRepository.getCurrentZone()
    }

    fun setCurrentZone(zone: Zone) {
        mCurrentZone.value = zone
    }

    fun deleteSubZone(id: Int) {
        subZoneRepository.delete(id)
    }
}
