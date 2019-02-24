package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.LampCategoryDao
import com.ihomey.linkuphome.data.db.RoomDao
import com.ihomey.linkuphome.data.db.SettingDao
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class SubZoneRepository @Inject constructor(private val subZoneDao: RoomDao, private val settingDao: SettingDao, private var appExecutors: AppExecutors) {

    fun getSubZones(deviceId: Int): LiveData<Resource<List<Room>>> {
        return object : NetworkBoundResource<List<Room>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Room>> {
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


    fun addRoom(currentSetting: Setting, currentZone: Zone , name: String, type: Int) {
        appExecutors.diskIO().execute {
            subZoneDao.insert(Room(currentSetting.nextGroupIndex,currentZone.id,name,type))
            currentSetting.nextGroupIndex = currentSetting.nextGroupIndex + 1
            settingDao.update(currentSetting)
        }
    }

    fun getRooms(deviceId: Int): LiveData<Resource<List<Room>>> {
        return object : NetworkBoundResource<List<Room>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Room>> {
                return subZoneDao.getRooms(deviceId)
            }
        }.asLiveData()
    }


    fun insert(setting: LampCategory,  name: String, type: Int,parentId: Int) {
        appExecutors.diskIO().execute {
//            subZoneDao.insert(Room(setting.nextGroupIndex, 1,1,name,type,"",ControlState()))
//            setting.nextGroupIndex = setting.nextGroupIndex + 1
//            lampCategoryDao.updateCategory(setting)
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

    fun updateSendTypes(roomId: Int,zoneId: Int) {
        appExecutors.diskIO().execute {
            subZoneDao.updateSendTypes(roomId,zoneId)
        }
    }


    fun updateRoomName(newName: String, id: Int) {
        appExecutors.diskIO().execute {
            subZoneDao.updateRoomName(newName,id)
        }
    }
}



