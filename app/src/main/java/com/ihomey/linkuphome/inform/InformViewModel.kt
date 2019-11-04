package com.ihomey.linkuphome.inform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.dl.DaggerAppComponent

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
