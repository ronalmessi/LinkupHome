package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.ModelDao
import com.ihomey.linkuphome.data.db.RoomDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.entity.Model
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class Model1Repository @Inject constructor(private val modelDao: ModelDao, private val subZoneDao: RoomDao, private val singleDeviceDao: SingleDeviceDao, private var appExecutors: AppExecutors) {

    fun getModels(deviceId: Int,zoneId:Int): LiveData<Resource<List<Model>>> {
        return object : NetworkBoundResource<List<Model>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Model>> {
                return modelDao.getModels(deviceId,zoneId)
            }
        }.asLiveData()
    }


    fun addModel(model: Model) {
        appExecutors.diskIO().execute {
            modelDao.insert(model)
            modelDao.updateType(model.deviceId,model.roomId,model.zoneId)
            subZoneDao.updateSendTypes(model.roomId,model.zoneId)
        }
    }


    fun deleteModel(deviceId: Int, roomId: Int, zoneId: Int) {
        appExecutors.diskIO().execute {
            modelDao.deleteModel(deviceId, roomId, zoneId)
            subZoneDao.updateSendTypes(roomId,zoneId)
        }
    }
}



