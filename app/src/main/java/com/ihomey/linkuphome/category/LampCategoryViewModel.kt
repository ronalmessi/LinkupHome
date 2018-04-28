package com.ihomey.linkuphome.category

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.iclass.soocsecretary.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.CategoryRepository
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import javax.inject.Inject


/**
 * Created by dongcaizheng on 2018/4/9.
 */
class LampCategoryViewModel : ViewModel() {

    private val needRefresh = MutableLiveData<Boolean>()
    private var addedCategoryResults: LiveData<Resource<List<LampCategory>>>
    private var unAddedCategoryResults: LiveData<Resource<List<LampCategory>>>

    @Inject
    lateinit var mCategoryRepository: CategoryRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
        addedCategoryResults = Transformations.switchMap(needRefresh, { _ -> mCategoryRepository.getCategories(1) })
        unAddedCategoryResults = Transformations.switchMap(needRefresh, { _ -> mCategoryRepository.getCategories(0) })
    }

    fun getAddedResults(): LiveData<Resource<List<LampCategory>>> {
        return addedCategoryResults
    }

    fun getUnAddedResults(): LiveData<Resource<List<LampCategory>>> {
        return unAddedCategoryResults
    }

    fun loadCategories() {
        needRefresh.value = true
    }

    fun updateCategory(lampCategory: LampCategory) {
        mCategoryRepository.updateCategory(lampCategory)
    }

}