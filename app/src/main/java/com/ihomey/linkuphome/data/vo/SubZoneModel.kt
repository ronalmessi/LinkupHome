package com.ihomey.linkuphome.data.vo

import androidx.room.Embedded
import androidx.room.Relation


class SubZoneModel {
    @Embedded var subZone: SubZone? = null
    @Relation(parentColumn = "id", entityColumn = "subZoneId")
    var modelList: List<Model1>? = null
}