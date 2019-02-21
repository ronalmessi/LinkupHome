package com.ihomey.linkuphome.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.ihomey.linkuphome.data.entity.Model
import com.ihomey.linkuphome.data.entity.SingleDevice


class ZoneSetting {
    @Embedded
    var zone: Zone? = null
    @Relation(parentColumn = "id", entityColumn = "zoneId")
    var settings: List<Setting> = listOf()
}