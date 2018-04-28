package com.ihomey.linkuphome.scan

import android.arch.lifecycle.ViewModel
import com.iclass.soocsecretary.component.DaggerAppComponent
import com.ihomey.linkuphome.data.repository.DataRepository
import org.json.JSONObject
import javax.inject.Inject

class ScanViewModel : ViewModel() {


    @Inject
    lateinit var mDataRepository: DataRepository

    init {
        DaggerAppComponent.builder().build().inject(this)
    }

    fun udateData(jsonObj: JSONObject) {
        mDataRepository.updateData(jsonObj)
    }
}