package com.ihomey.linkuphome.scan

import androidx.lifecycle.ViewModel
import com.ihomey.linkuphome.data.repository.DataRepository
import com.ihomey.linkuphome.dl.DaggerAppComponent
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