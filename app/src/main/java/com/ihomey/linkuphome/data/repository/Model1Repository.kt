package com.ihomey.linkuphome.data.repository

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.Model1Dao
import com.ihomey.linkuphome.data.db.ModelDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.db.SubZoneDao
import com.ihomey.linkuphome.data.vo.Model
import com.ihomey.linkuphome.data.vo.Model1
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class Model1Repository @Inject constructor(private val modelDao: Model1Dao, private val subZoneDao: SubZoneDao, private val singleDeviceDao: SingleDeviceDao, private var appExecutors: AppExecutors) {

    fun getModels(deviceId: Int): LiveData<Resource<List<Model1>>> {
        return object : NetworkBoundResource<List<Model1>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Model1>> {
                return modelDao.getModels(deviceId)
            }
        }.asLiveData()
    }


    fun addModel(model: Model1) {
        appExecutors.diskIO().execute {
            modelDao.insert(model)
            modelDao.updateType(model.deviceId,model.subZoneId)
            subZoneDao.updateSendTypes(model.subZoneId)
        }
    }


    fun deleteModel(deviceId: Int, groupId: Int, groupIndex: Int) {
        appExecutors.diskIO().execute {
            modelDao.deleteModel(deviceId, groupId, groupIndex)
            subZoneDao.updateSendTypes(groupId)
        }
    }
}



