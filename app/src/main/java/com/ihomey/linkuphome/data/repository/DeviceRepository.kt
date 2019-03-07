package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.data.api.AbsentLiveData
import com.ihomey.linkuphome.data.api.ApiResult
import com.ihomey.linkuphome.data.api.ApiService
import com.ihomey.linkuphome.data.api.NetworkBoundResource
import com.ihomey.linkuphome.data.db.SettingDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class DeviceRepository @Inject constructor(private var apiService: ApiService, private val singleDeviceDao: SingleDeviceDao, private val settingDao: SettingDao, private var appExecutors: AppExecutors) {


    fun register(registerDTO: RegisterVO): LiveData<Resource<String>> {
        return object : NetworkBoundResource<String>(appExecutors) {
            override fun saveCallResult(item: String) {

            }

            override fun shouldFetch(data: String?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<String> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResult<String>> {
                return apiService.register(registerDTO)
            }
        }.asLiveData()
    }


    fun getDevices(): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getDevices()
            }
        }.asLiveData()
    }

    fun getDeviceModels(lampGroupType: Int): LiveData<Resource<List<DeviceModel>>> {
        return object : DbBoundResource<List<DeviceModel>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<DeviceModel>> {
                return singleDeviceDao.getDeviceModels(lampGroupType)
            }
        }.asLiveData()
    }



    fun getBondedDevices(deviceType: Int, groupId: Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getBondedDevices(deviceType, groupId)
            }
        }.asLiveData()
    }


    fun getUnBondedDevices(deviceType: Int, groupId: Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getUnBondedDevices(deviceType, groupId)
            }
        }.asLiveData()
    }


    fun getDevices(zoneId: Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getDevices(zoneId)
            }
        }.asLiveData()
    }


    fun getDevices(zoneId: Int,type:Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getDevices(zoneId,type)
            }
        }.asLiveData()
    }


//    fun getUnBondedDevices(subZoneId: Int): LiveData<Resource<List<SingleDevice>>> {
//        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
//            override fun loadFromDb(): LiveData<List<SingleDevice>> {
//                return singleDeviceDao.getUnBondedDevices(subZoneId)
//            }
//        }.asLiveData()
//    }


    fun getBindedDevices(zoneId: Int,roomId:Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getBindedDevices(zoneId,roomId)
            }
        }.asLiveData()
    }

    fun getUnBindedDevices(zoneId: Int,roomId:Int): LiveData<Resource<List<SingleDevice>>> {
        return object : DbBoundResource<List<SingleDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SingleDevice>> {
                return singleDeviceDao.getUnBindedDevices(zoneId)
            }
        }.asLiveData()
    }


//    fun getDevices(deviceType: Int, id: Int): LiveData<Resource<ControlDevice>> {
//        return object : DbBoundResource<ControlDevice>(appExecutors) {
//            override fun loadFromDb(): LiveData<ControlDevice> {
//                return singleDeviceDao.getDeviceDistinctLiveData(deviceType, id)
//            }
//        }.asLiveData()
//    }

//    fun addSingleDevice(setting: LampCategory, singleDevice: SingleDevice) {
//        appExecutors.diskIO().execute {
//            singleDeviceDao.insert(singleDevice)
//            setting.nextDeviceIndex = setting.nextDeviceIndex + 1
//            lampCategoryDao.updateCategory(setting)
//        }
//    }

    fun addSingleDevice(currentSetting: Setting, singleDevice: SingleDevice) {
        appExecutors.diskIO().execute {
            singleDeviceDao.insert(singleDevice)
            currentSetting.nextDeviceIndex = currentSetting.nextDeviceIndex + 1
            settingDao.update(currentSetting)
        }
    }


    fun deleteSingleDevice(deviceType: Int, singleDeviceId: Int) {
        appExecutors.diskIO().execute {
//            var lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$deviceType", -1)
//            lastUsedDeviceId = -1
            singleDeviceDao.deleteById(singleDeviceId)
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

    fun updateSingleDeviceName(singleDeviceId: Int, name: String) {
        appExecutors.diskIO().execute {
            singleDeviceDao.updateDeviceName(name, singleDeviceId)
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



