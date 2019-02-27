package com.ihomey.linkuphome.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.GroupRepository
import com.ihomey.linkuphome.data.vo.DeviceModel
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class ShareViewModel : ViewModel() {

//    private var settingResults: LiveData<Resource<List<LampCategory>>>
    private var deviceResults: LiveData<Resource<List<DeviceModel>>>
    private var groupResults: LiveData<Resource<List<GroupDevice>>>


    private val loadDeviceType = MutableLiveData<Int>()
    private val loadGroupDeviceType = MutableLiveData<Int>()
    private val loadSettingType = MutableLiveData<Int>()

//    @Inject
//    lateinit var mCategoryRepository: CategoryRepository

    @Inject
    lateinit var mGroupRepository: GroupRepository

    @Inject
    lateinit var mDeviceRepository: DeviceRepository


    init {
        DaggerAppComponent.builder().build().inject(this)
//        settingResults = Transformations.switchMap(loadSettingType, { input ->
//            mCategoryRepository.getSettings(input)
//
//        })
        deviceResults = Transformations.switchMap(loadDeviceType, { input ->
            mDeviceRepository.getDeviceModels(input)

        })
        groupResults = Transformations.switchMap(loadGroupDeviceType, { input ->
            mGroupRepository.getGroups(input)
        })
    }

    fun getGroupResults(): LiveData<Resource<List<GroupDevice>>> {
        return groupResults
    }

    fun getDeviceResults(): LiveData<Resource<List<DeviceModel>>> {
        return deviceResults
    }

//    fun getSettingResults(): LiveData<Resource<List<LampCategory>>> {
//        return settingResults
//    }


    fun loadGroups(lampCategoryType: Int) {
        loadGroupDeviceType.value = lampCategoryType
    }

    fun loadDevices(lampCategoryType: Int) {
        loadDeviceType.value = lampCategoryType
    }

    fun loadSettings(lampCategoryType: Int) {
        loadSettingType.value = lampCategoryType
    }

}