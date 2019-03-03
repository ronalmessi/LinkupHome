package com.ihomey.linkuphome.zone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.repository.RoomRepository
import com.ihomey.linkuphome.data.repository.ZoneRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ZonesViewModel : ViewModel() {

    @Inject
    lateinit var zoneRepository: ZoneRepository

    @Inject
    lateinit var roomRepository: RoomRepository

    private val mCurrentZone = MutableLiveData<Zone>()

//    val subZonesResult: LiveData<Resource<List<SubZoneModel>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
//        subZonesResult = Transformations.switchMap(mCurrentZone) { input ->
//            roomRepository.getSubZoneModels(input.id)
//        }
    }

    fun setCurrentZone(zone: Zone) {
        mCurrentZone.value = zone
    }

    fun deleteSubZone(id: Int) {
        roomRepository.delete(id)
    }

    fun updateSubZone(subZone: Room) {
        subZone.state?.let { roomRepository.updateSubZoneState(subZone) }
    }
}
