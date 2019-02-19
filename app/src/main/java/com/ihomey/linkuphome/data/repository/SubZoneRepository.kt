package com.ihomey.linkuphome.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.LampCategoryDao
import com.ihomey.linkuphome.data.db.SubZoneDao
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class SubZoneRepository @Inject constructor(private val subZoneDao: SubZoneDao, private val lampCategoryDao: LampCategoryDao, private var appExecutors: AppExecutors) {

    fun getSubZones(deviceId: Int): LiveData<Resource<List<SubZone>>> {
        return object : NetworkBoundResource<List<SubZone>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SubZone>> {
                return subZoneDao.getSubZones(deviceId)
            }
        }.asLiveData()
    }

    fun getSubZoneModels(deviceId: Int): LiveData<Resource<List<SubZoneModel>>> {
        return object : NetworkBoundResource<List<SubZoneModel>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<SubZoneModel>> {
                return subZoneDao.getSubZoneModels(deviceId)
            }
        }.asLiveData()
    }


    fun insert(setting: LampCategory,  name: String, type: Int,parentId: Int) {
        appExecutors.diskIO().execute {
            subZoneDao.insert(SubZone(setting.nextGroupIndex, Device(name,type),parentId,"",ControlState()))
            setting.nextGroupIndex = setting.nextGroupIndex + 1
            lampCategoryDao.updateCategory(setting)
        }
    }

    fun delete(id: Int) {
        appExecutors.diskIO().execute {
            subZoneDao.delete(id)
        }
    }

    fun updateSubZoneState(subZoneId: Int,state: ControlState) {
        appExecutors.diskIO().execute {
            subZoneDao.updateSubZoneState(subZoneId,state.on,state.light,state.changeMode,state.colorPosition,state.colorTemperature,state.brightness,state.sceneMode,state.openTimer,state.closeTimer,state.openTimerOn,state.closeTimerOn)
        }
    }


    fun updateSubZoneName(newName: String, id: Int) {
        appExecutors.diskIO().execute {
            subZoneDao.updateSubZoneName(newName,id)
        }
    }
}



