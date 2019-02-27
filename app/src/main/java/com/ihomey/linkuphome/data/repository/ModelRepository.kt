package com.ihomey.linkuphome.data.repository

import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.ModelDao
import com.ihomey.linkuphome.data.entity.Model
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class ModelRepository @Inject constructor(private val modelDao: ModelDao, private var appExecutors: AppExecutors) {
//
//    fun getModels(deviceId: Int): LiveData<Resource<List<Model>>> {
//        return object : NetworkBoundResource<List<Model>>(appExecutors) {
//            override fun loadFromDb(): LiveData<List<Model>> {
//                return modelDao.getModels(deviceId)
//            }
//        }.asLiveData()
//    }


    fun addModel(model: Model) {
        appExecutors.diskIO().execute {
//            modelDao.insert(model)
        }
    }

    fun deleteModel(deviceId: Int,groupId: Int,groupIndex:Int) {
        appExecutors.diskIO().execute {
//            modelDao.deleteModel(deviceId, groupId, groupIndex)
        }
    }
}



