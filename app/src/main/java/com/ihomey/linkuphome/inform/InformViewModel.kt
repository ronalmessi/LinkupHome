package com.ihomey.linkuphome.inform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.repository.*
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.dl.DaggerAppComponent
import javax.inject.Inject

class InformViewModel : ViewModel() {

     val mCurrentZoneId = MutableLiveData<Int>()

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    //zone
    fun setCurrentZoneId(zoneId: Int?) {
        mCurrentZoneId.value = zoneId
    }

}
