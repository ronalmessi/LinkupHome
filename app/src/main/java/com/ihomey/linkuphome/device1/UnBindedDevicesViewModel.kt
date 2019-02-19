package com.ihomey.linkuphome.device1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject

class UnBindedDevicesViewModel : ViewModel() {

    @Inject
    lateinit var deviceRepository: DeviceRepository

    @Inject
    lateinit var modelRepository: Model1Repository

    @Inject
    lateinit var subZoneRepository: SubZoneRepository

    val devicesResult: LiveData<Resource<List<SingleDevice>>>

    val mSubZoneId = MutableLiveData<Int>()

    init {
        DaggerAppComponent.builder().build().inject(this)
        devicesResult = Transformations.switchMap(mSubZoneId) { input ->
            deviceRepository.getUnBondedDevices(input)
        }
    }

    fun setSubZoneId(id: Int) {
        mSubZoneId.value=id
    }

    fun createModel(model: Model1) {
        modelRepository.addModel(model)
    }


}
