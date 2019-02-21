package com.ihomey.linkuphome.data.vo

import androidx.room.Embedded
import androidx.room.Relation
import com.ihomey.linkuphome.data.entity.Model
import com.ihomey.linkuphome.data.entity.SingleDevice


class DeviceModel {
    @Embedded var device: SingleDevice? = null
    @Relation(parentColumn = "id", entityColumn = "deviceId")
    var modelList: List<Model>? = null
}