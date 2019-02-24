package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.GroupDeviceDao
import com.ihomey.linkuphome.data.db.LampCategoryDao
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class GroupRepository @Inject constructor(private val lampGroupDao: GroupDeviceDao, private val lampCategoryDao: LampCategoryDao, private var appExecutors: AppExecutors) {

    fun getGroups(lampGroupType: Int): LiveData<Resource<List<GroupDevice>>> {
        return object : NetworkBoundResource<List<GroupDevice>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<GroupDevice>> {
                return lampGroupDao.getGroups(lampGroupType)
            }
        }.asLiveData()
    }

    fun getGroup(deviceType:Int,id: Int): LiveData<Resource<ControlDevice>> {
        return object : NetworkBoundResource<ControlDevice>(appExecutors) {
            override fun loadFromDb(): LiveData<ControlDevice> {
                return lampGroupDao.getGroupDistinctLiveData(deviceType,id)
            }
        }.asLiveData()
    }

    fun updateGroupName(deviceType:Int,groupDeviceId: Int, name: String) {
        appExecutors.diskIO().execute {
            lampGroupDao.updateGroupName(deviceType,name, groupDeviceId)
        }
    }

    fun addGroup(setting: LampCategory, settingForType: LampCategory, lampGroup: GroupDevice) {
        appExecutors.diskIO().execute {
            lampGroupDao.insert(lampGroup)
            setting.nextGroupIndex = setting.nextGroupIndex + 1
            settingForType.nextGroupIndex = settingForType.nextGroupIndex + 1
            lampCategoryDao.updateCategory(setting)
            lampCategoryDao.updateCategory(settingForType)
        }
    }

    fun deleteGroup(lampGroup: GroupDevice) {
        appExecutors.diskIO().execute {
            var lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_${lampGroup.device?.type}", -1)
            lastUsedDeviceId = -1
            lampGroupDao.delete(lampGroup)
        }
    }

    fun deleteGroupByType(type: Int) {
        appExecutors.diskIO().execute {
            lampGroupDao.deleteByType(type)
        }
    }

    fun updateDeviceSceneMode(deviceType:Int,deviceId: Int,sceneMode:Int) {
        appExecutors.diskIO().execute {
            lampGroupDao.updateDeviceSceneMode(deviceType,deviceId, sceneMode)
        }
    }

    fun updateDevice(groupDevice:GroupDevice) {
        appExecutors.diskIO().execute {
            lampGroupDao.update(groupDevice)
        }
    }
}



