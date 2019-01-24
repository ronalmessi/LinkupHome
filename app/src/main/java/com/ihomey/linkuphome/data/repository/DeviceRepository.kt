package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.LampCategoryDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class DeviceRepository @Inject constructor(private val singleDeviceDao: SingleDeviceDao, val lampCategoryDao: LampCategoryDao, private var appExecutors: AppExecutors) {

    fun getDevices(lampGroupType: Int): LiveData<Resource<List<SingleDevice>>> {
        return object : NetworkBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getDevices(lampGroupType)
            }
        }.asLiveData()
    }

    fun getDeviceModels(lampGroupType: Int): LiveData<Resource<List<DeviceModel>>> {
        return object : NetworkBoundResource<List<DeviceModel>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<DeviceModel>> {
                return singleDeviceDao.getDeviceModels(lampGroupType)
            }
        }.asLiveData()
    }

    fun getAllControlDevices(lampGroupType: Int): LiveData<Resource<List<ControlDevice>>> {
        return object : NetworkBoundResource<List<ControlDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ControlDevice>> {
                return singleDeviceDao.getControlDevices(lampGroupType)
            }
        }.asLiveData()
    }

    fun getBondedDevices(deviceType: Int, groupId: Int): LiveData<Resource<List<SingleDevice>>> {
        return object : NetworkBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getBondedDevices(deviceType, groupId)
            }
        }.asLiveData()
    }


    fun getUnBondedDevices(deviceType: Int, groupId: Int): LiveData<Resource<List<SingleDevice>>> {
        return object : NetworkBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getUnBondedDevices(deviceType, groupId)
            }
        }.asLiveData()
    }

    fun getDevice(deviceType: Int, id: Int): LiveData<Resource<ControlDevice>> {
        return object : NetworkBoundResource<ControlDevice>(appExecutors) {
            override fun loadFromDb(): LiveData<ControlDevice> {
                return singleDeviceDao.getDeviceDistinctLiveData(deviceType, id)
            }
        }.asLiveData()
    }

    fun addSingleDevice(setting: LampCategory, singleDevice: SingleDevice) {
        appExecutors.diskIO().execute {
            singleDeviceDao.insert(singleDevice)
            setting.nextDeviceIndex = setting.nextDeviceIndex + 1
            lampCategoryDao.updateCategory(setting)
        }
    }

    fun deleteSingleDevice(deviceType: Int, singleDeviceId: Int) {
        appExecutors.diskIO().execute {
            var lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$deviceType", -1)
            lastUsedDeviceId = -1
            singleDeviceDao.deleteById(deviceType, singleDeviceId)
        }
    }

    fun deleteDevicesByType(type: Int) {
        appExecutors.diskIO().execute {
            singleDeviceDao.deleteByType(type)
        }
    }

    fun updateSingleDeviceName(deviceType: Int, singleDeviceId: Int, name: String) {
        appExecutors.diskIO().execute {
            singleDeviceDao.updateDeviceName(deviceType, name, singleDeviceId)
        }
    }

    fun updateDeviceSceneMode(deviceType: Int, deviceId: Int, sceneMode: Int) {
        appExecutors.diskIO().execute {
            singleDeviceDao.updateDeviceSceneMode(deviceType, deviceId, sceneMode)
        }
    }

    fun updateDeviceState(singleDeviceId: Int,state: ControlState) {
        appExecutors.diskIO().execute {
            singleDeviceDao.updateDeviceState(singleDeviceId,state.on,state.light,state.changeMode,state.colorPosition,state.colorTemperature,state.brightness,state.sceneMode,state.openTimer,state.closeTimer,state.openTimerOn,state.closeTimerOn)
        }
    }

    fun setLastUsedDeviceId(deviceId: Int, deviceType: Int) {
        appExecutors.diskIO().execute {
            var lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$deviceType", -1)
            lastUsedDeviceId = deviceId
        }
    }
}



