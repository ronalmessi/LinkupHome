package com.ihomey.linkuphome.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.ihomey.linkuphome.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject


/**
 * Created by dongcaizheng on 2018/4/9.
 */
class MainViewModel : ViewModel() {


    private val mCategoryType = MutableLiveData<Int>()
    private var currentControlDeviceInfo = MutableLiveData<DeviceInfo>()
    private val loadGroupInfo = MutableLiveData<DeviceInfo>()
    private val modelDeviceId = MutableLiveData<Int>()

    private var categoryResults: LiveData<Resource<List<LampCategory>>>? = null
    private var modelResults: LiveData<Resource<List<Model>>>
    private var deviceResults: LiveData<Resource<List<SingleDevice>>>
    private val controlDeviceResults: LiveData<Resource<List<ControlDevice>>>
//    val alarmResults: LiveData<Resource<List<Alarm>>>
    private var bondedDeviceResults: LiveData<Resource<List<SingleDevice>>>
    private var unBondedDeviceResults: LiveData<Resource<List<SingleDevice>>>
    private var currentControlDevice: LiveData<Resource<ControlDevice>>

    private var localSetting: LiveData<Resource<LampCategory>>

    private val bridgeState = MutableLiveData<Boolean>()

    @Inject
    lateinit var mCategoryRepository: CategoryRepository
    @Inject
    lateinit var mGroupRepository: GroupRepository
    @Inject
    lateinit var mDeviceRepository: DeviceRepository
    @Inject
    lateinit var modelRepository: ModelRepository
    @Inject
    lateinit var malarmRepository: AlarmRepository


    init {
        DaggerAppComponent.builder().build().inject(this)
        localSetting = Transformations.switchMap(mCategoryType) { input ->
            mCategoryRepository.getLocalSetting(input)
        }
        deviceResults = Transformations.switchMap(mCategoryType) { input ->
            mDeviceRepository.getDevices(input)
        }
        controlDeviceResults = Transformations.switchMap(mCategoryType) { input ->
            mDeviceRepository.getAllControlDevices(input)
        }
        modelResults = Transformations.switchMap(modelDeviceId) { input ->
            modelRepository.getModels(input)
        }
        bondedDeviceResults = Transformations.switchMap(loadGroupInfo) { input ->
            mDeviceRepository.getBondedDevices(input.deviceType, input.deviceId)
        }
        unBondedDeviceResults = Transformations.switchMap(loadGroupInfo) { input ->
            mDeviceRepository.getUnBondedDevices(input.deviceType, input.deviceId)
        }
        currentControlDevice = Transformations.switchMap(currentControlDeviceInfo) { input ->
            mDeviceRepository.setLastUsedDeviceId(input.deviceId, input.deviceType)
            if ((input.deviceId in 1..32768) && input.deviceType < 5) {
                mGroupRepository.getGroup(input.deviceType, input.deviceId)
            } else {
                mDeviceRepository.getDevice(input.deviceType, input.deviceId)
            }
        }
//        alarmResults = Transformations.switchMap(currentControlDeviceInfo) { input ->
//            malarmRepository.getAlarms(input.deviceId)
//        }
    }

    fun getCategoryResults(): LiveData<Resource<List<LampCategory>>>? {
        if (categoryResults == null) {
            categoryResults = mCategoryRepository.getCategories()
        }
        return categoryResults
    }

    fun getGroupResults(type: Int): LiveData<Resource<List<GroupDevice>>>? {
        return mGroupRepository.getGroups(type)
    }

    fun getAlarmResults(deviceId: Int): LiveData<Resource<List<Alarm>>>? {
        return  malarmRepository.getAlarms(deviceId)
    }


    fun getDeviceResults(): LiveData<Resource<List<SingleDevice>>> {
        return deviceResults
    }

    fun getControlDeviceResults(): LiveData<Resource<List<ControlDevice>>> {
        return controlDeviceResults
    }

    fun getModelResults(): LiveData<Resource<List<Model>>> {
        return modelResults
    }

    fun getBondedDeviceResults(): LiveData<Resource<List<SingleDevice>>> {
        return bondedDeviceResults
    }

    fun getUnBondedDeviceResults(): LiveData<Resource<List<SingleDevice>>> {
        return unBondedDeviceResults
    }

    fun getGlobalSetting(): LiveData<Resource<LampCategory>>? {
        return mCategoryRepository.getGlobalSetting()
    }

    fun getLocalSetting(): LiveData<Resource<LampCategory>> {
        return localSetting
    }

    fun getCurrentControlDevice(): LiveData<Resource<ControlDevice>> {
        return currentControlDevice
    }

    fun updateCategory(lampCategory: LampCategory) {
        mCategoryRepository.updateCategory(lampCategory)
    }

    fun loadData(categoryType: Int) {
        mCategoryType.value = categoryType
    }

    fun loadBoundedDevices(deviceInfo: DeviceInfo) {
        loadGroupInfo.value = deviceInfo
    }

    fun addSingleDevice(setting: LampCategory, singleDevice: SingleDevice) {
        mDeviceRepository.addSingleDevice(setting, singleDevice)
    }

    fun addGroup(globalSetting: LampCategory, localSetting: LampCategory, name: String, type: Int) {
        mGroupRepository.addGroup(globalSetting, localSetting, GroupDevice(globalSetting.nextGroupIndex, Device(name + localSetting.nextGroupIndex, type), ControlState()))
    }

    fun deleteGroup(lampGroup: GroupDevice) {
        mGroupRepository.deleteGroup(lampGroup)
    }

    fun deleteSingleDevice(deviceType: Int, singleDeviceId: Int) {
        mDeviceRepository.deleteSingleDevice(deviceType, singleDeviceId)
    }

    fun setCurrentControlDeviceInfo(deviceInfo: DeviceInfo) {
        currentControlDeviceInfo.value = deviceInfo
    }

    fun updateDeviceName(deviceType: Int, deviceId: Int, deviceName: String) {
        if ((deviceId in 1..32768) && deviceType < 5) {
            mGroupRepository.updateGroupName(deviceType, deviceId, deviceName)
        } else {
            mDeviceRepository.updateSingleDeviceName(deviceType, deviceId, deviceName)
        }
    }

    fun updateDevice(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            if (controlDevice.id in 1..32768 && controlDevice.device.type < 5) {
                mGroupRepository.updateDevice(GroupDevice(controlDevice.id, controlDevice.device, controlDevice.state))
            } else {
                mDeviceRepository.updateDeviceState(controlDevice.id, controlDevice.state)
            }
        }
    }

    fun removeAllDevices(lampCategoryType: Int) {
        mDeviceRepository.deleteDevicesByType(lampCategoryType)
        removeAllGroupDevices(lampCategoryType)
    }

    fun removeAllGroupDevices(lampCategoryType: Int) {
        mGroupRepository.deleteGroupByType(lampCategoryType)
        mCategoryRepository.updateGroupIndex(lampCategoryType, 1)
    }

    fun loadModels(deviceId: Int) {
        modelDeviceId.value = deviceId
    }

    fun addModel(model: Model) {
        modelRepository.addModel(model)
    }

    fun deleteModel(deviceId: Int, groupId: Int, groupIndex: Int) {
        modelRepository.deleteModel(deviceId, groupId, groupIndex)
    }

    fun getBridgeState(): MutableLiveData<Boolean> {
        return bridgeState
    }

    fun setBridgeState(connected: Boolean) {
        bridgeState.value = connected
    }

}