package com.ihomey.linkuphome.control

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.iclass.soocsecretary.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.CategoryRepository
import com.ihomey.linkuphome.data.repository.DeviceRepository
import com.ihomey.linkuphome.data.repository.GroupRepository
import com.ihomey.linkuphome.data.repository.ModelRepository
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject


/**
 * Created by dongcaizheng on 2018/4/9.
 */
class MeshControlViewModel : ViewModel() {

    private var groupResults: LiveData<Resource<List<GroupDevice>>>
    private var deviceResults: LiveData<Resource<List<SingleDevice>>>
    private var settingResults: LiveData<Resource<List<LampCategory>>>
    private var modelResults: LiveData<Resource<List<Model>>>
    private val controlDeviceResults: LiveData<Resource<List<ControlDevice>>>
    private var currentControlDevice: LiveData<Resource<ControlDevice>>

    private var bondedDeviceResults: LiveData<Resource<List<SingleDevice>>>
    private var unBondedDeviceResults: LiveData<Resource<List<SingleDevice>>>

    private var currentControlDeviceInfo = MutableLiveData<DeviceInfo>()
    private val loadDeviceType = MutableLiveData<Int>()
    private val modelDeviceId = MutableLiveData<Int>()
    private val loadGroupDeviceType = MutableLiveData<Int>()
    private val loadSettingType = MutableLiveData<Int>()
    private val loadGroupInfo = MutableLiveData<DeviceInfo>()

    private val isBridgeConnected = MutableLiveData<Boolean>()

    private val enableDiscoverDevices = MutableLiveData<Boolean>()
    private val enableAssociateDevice = MutableLiveData<Int>()
    private val enableRemoveDevice = MutableLiveData<SingleDevice>()

    @Inject
    lateinit var mGroupRepository: GroupRepository

    @Inject
    lateinit var mDeviceRepository: DeviceRepository

    @Inject
    lateinit var mCategoryRepository: CategoryRepository

    @Inject
    lateinit var modelRepository: ModelRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
        groupResults = Transformations.switchMap(loadGroupDeviceType, { input ->
            mGroupRepository.getGroups(input)
        })

        deviceResults = Transformations.switchMap(loadDeviceType, { input ->
            mDeviceRepository.getDevices(input)
        })

        modelResults = Transformations.switchMap(modelDeviceId, { input ->
            modelRepository.getModels(input)
        })

        bondedDeviceResults = Transformations.switchMap(loadGroupInfo, { input ->
            mDeviceRepository.getBondedDevices(input.deviceType, input.deviceId)
        })

        unBondedDeviceResults = Transformations.switchMap(loadGroupInfo, { input ->
            mDeviceRepository.getUnBondedDevices(input.deviceType, input.deviceId)
        })

        settingResults = Transformations.switchMap(loadSettingType, { input ->
            mCategoryRepository.getSettings(input)
        })

        controlDeviceResults = Transformations.switchMap(loadSettingType, { input ->
            mDeviceRepository.getAllControlDevices(input)
        })

        currentControlDevice = Transformations.switchMap(currentControlDeviceInfo, { input ->
            if (input.deviceId in 1..32768) {
                mGroupRepository.getGroup(input.deviceType, input.deviceId)
            } else {
                mDeviceRepository.getDevice(input.deviceType, input.deviceId)
            }
        })
    }

    fun getGroupResults(): LiveData<Resource<List<GroupDevice>>> {
        return groupResults
    }

    fun getDeviceResults(): LiveData<Resource<List<SingleDevice>>> {
        return deviceResults
    }

    fun getSettingResults(): LiveData<Resource<List<LampCategory>>> {
        return settingResults
    }

    fun getControlDeviceResults(): LiveData<Resource<List<ControlDevice>>> {
        return controlDeviceResults
    }

    fun getBondedDeviceResults(): LiveData<Resource<List<SingleDevice>>> {
        return bondedDeviceResults
    }

    fun getUnBondedDeviceResults(): LiveData<Resource<List<SingleDevice>>> {
        return unBondedDeviceResults
    }

    fun getDiscoverDevicesState(): LiveData<Boolean> {
        return enableDiscoverDevices
    }

    fun getAssociateDeviceState(): LiveData<Int> {
        return enableAssociateDevice
    }

    fun getRemoveDeviceState(): LiveData<SingleDevice> {
        return enableRemoveDevice
    }

    fun getModelResults(): LiveData<Resource<List<Model>>> {
        return modelResults
    }

    fun getCurrentControlDevice(): LiveData<Resource<ControlDevice>> {
        return currentControlDevice
    }

    fun isBridgeConnected(): LiveData<Boolean> {
        return isBridgeConnected
    }

    fun setBridgeConnecState(isConnected: Boolean) {
        isBridgeConnected.value = isConnected
    }

    fun updateDeviceName(deviceType: Int, deviceId: Int, deviceName: String) {
        if (deviceId in 1..32768) {
            mGroupRepository.updateGroupName(deviceType, deviceId, deviceName)
        } else {
            mDeviceRepository.updateSingleDeviceName(deviceType, deviceId, deviceName)
        }
    }

    fun addGroup(setting: LampCategory, settingForType: LampCategory, name: String, type: Int) {
        mGroupRepository.addGroup(setting, settingForType, GroupDevice(setting.nextGroupIndex, Device(name + settingForType.nextGroupIndex, type), ControlState()))
    }

    fun deleteGroup(lampGroup: GroupDevice) {
        mGroupRepository.deleteGroup(lampGroup)
    }

    fun loadGroups(lampCategoryType: Int) {
        loadGroupDeviceType.value = lampCategoryType
    }

    fun addDevice(setting: LampCategory, singleDevice: SingleDevice) {
        mDeviceRepository.addSingleDevice(setting, singleDevice)
    }

    fun loadDevices(lampCategoryType: Int) {
        loadDeviceType.value = lampCategoryType
    }

    fun setCurrentControlDeviceInfo(deviceInfo: DeviceInfo) {
        currentControlDeviceInfo.value = deviceInfo
    }

    fun setLastUsedDeviceId(deviceId: Int, deviceType: Int) {
        mDeviceRepository.setLastUsedDeviceId(deviceId, deviceType)
    }

    fun setModelDeviceId(deviceId: Int) {
        modelDeviceId.value = deviceId
    }

    fun addModel(model: Model) {
        modelRepository.addModel(model)
    }

    fun deleteModel(deviceId: Int, groupId: Int, groupIndex: Int) {
        modelRepository.deleteModel(deviceId, groupId, groupIndex)
    }

    fun setDeviceDiscoveryFilterEnabled(enabled: Boolean) {
        enableDiscoverDevices.value = enabled
    }

    fun associateDevice(uuidHash: Int) {
        enableAssociateDevice.value = uuidHash
    }

    fun removeDevice(singleDevice: SingleDevice) {
        enableRemoveDevice.value = singleDevice
    }

    fun loadSetting(lampCategoryType: Int) {
        loadSettingType.value = lampCategoryType
    }

    fun loadBoundedDevices(deviceInfo: DeviceInfo) {
        loadGroupInfo.value = deviceInfo
    }

    fun removeDevice(deviceType: Int, singleDeviceId: Int) {
        mDeviceRepository.deleteSingleDevice(deviceType, singleDeviceId)
    }

    fun removeAllDevices(lampCategoryType: Int) {
        mDeviceRepository.deleteDevicesByType(lampCategoryType)
        removeAllGroupDevices(lampCategoryType)
    }

    fun removeAllGroupDevices(lampCategoryType: Int) {
        mGroupRepository.deleteGroupByType(lampCategoryType)
        mCategoryRepository.updateGroupIndex(lampCategoryType, 1)
    }

    fun updateDevice(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            if (controlDevice.id in 1..32768) {
                mGroupRepository.updateDevice(GroupDevice(controlDevice.id, controlDevice.device, controlDevice.state))
            } else {
                mDeviceRepository.updateDeviceState(controlDevice.id,controlDevice.state)
            }
        }

    }

}