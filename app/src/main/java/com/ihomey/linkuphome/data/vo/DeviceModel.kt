package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation


class DeviceModel {
    @Embedded var device: SingleDevice? = null
    @Relation(parentColumn = "id", entityColumn = "deviceId")
    var modelList: List<Model>? = null
}