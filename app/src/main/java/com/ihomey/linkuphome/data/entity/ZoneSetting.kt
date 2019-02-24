package com.ihomey.linkuphome.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.ihomey.linkuphome.data.entity.Model
import com.ihomey.linkuphome.data.entity.SingleDevice


class ZoneSetting {

    var zone: Zone? = null

    var settings: List<Setting> = listOf()
}