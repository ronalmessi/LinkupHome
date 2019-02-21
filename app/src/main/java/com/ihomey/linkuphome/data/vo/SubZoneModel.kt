package com.ihomey.linkuphome.data.vo

import androidx.room.Embedded
import androidx.room.Relation
import com.ihomey.linkuphome.data.entity.Room


class SubZoneModel {
    @Embedded var subZone: Room? = null
    @Relation(parentColumn = "id", entityColumn = "subZoneId")
    var modelList: List<Model1>? = null
}