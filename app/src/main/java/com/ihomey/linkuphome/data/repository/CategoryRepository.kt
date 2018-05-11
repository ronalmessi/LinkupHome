package com.ihomey.linkuphome.data.repository

import android.arch.lifecycle.LiveData
import com.ihomey.linkuphome.AppExecutors
import com.ihomey.linkuphome.data.db.LampCategoryDao
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class CategoryRepository @Inject constructor(private val lampCategoryDao: LampCategoryDao, private var appExecutors: AppExecutors) {


    fun getCategories(): LiveData<Resource<List<LampCategory>>> {
        return object : NetworkBoundResource<List<LampCategory>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<LampCategory>> {
                return lampCategoryDao.getCategories()
            }
        }.asLiveData()
    }

    fun getSettings(type: Int): LiveData<Resource<List<LampCategory>>> {
        return object : NetworkBoundResource<List<LampCategory>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<LampCategory>> {
                return lampCategoryDao.getSettings(arrayOf(-1,type))
            }
        }.asLiveData()
    }

    fun getGlobalSetting(): LiveData<Resource<LampCategory>> {
        return object : NetworkBoundResource<LampCategory>(appExecutors) {
            override fun loadFromDb(): LiveData<LampCategory> {
                return lampCategoryDao.getGlobalSetting()
            }
        }.asLiveData()
    }

    fun getLocalSetting(type: Int): LiveData<Resource<LampCategory>> {
        return object : NetworkBoundResource<LampCategory>(appExecutors) {
            override fun loadFromDb(): LiveData<LampCategory> {
                return lampCategoryDao.getLocalSetting(type)
            }
        }.asLiveData()
    }

    fun updateCategory(lampCategory: LampCategory) {
        appExecutors.diskIO().execute {
            lampCategoryDao.updateCategory(lampCategory)
        }
    }

    fun updateGroupIndex(lampCategoryType: Int,groupIndex:Int) {
        appExecutors.diskIO().execute {
            lampCategoryDao.updateGroupIndex(groupIndex,lampCategoryType)
        }

    }
}



