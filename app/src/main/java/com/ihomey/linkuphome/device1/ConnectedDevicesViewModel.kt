package com.ihomey.linkuphome.device1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel;
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject

class ConnectedDevicesViewModel : ViewModel() {

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    private val mCurrentZoneId = MutableLiveData<Int>()

    val devicesResult: LiveData<Resource<List<SingleDevice>>>

    init {
        DaggerAppComponent.builder().build().inject(this)
        devicesResult = Transformations.switchMap(mCurrentZoneId) { input ->
            mDeviceRepository.getDevices(input)
        }
    }

    fun setCurrentZoneId(zoneId: Int) {
        mCurrentZoneId.value = zoneId
    }


}
