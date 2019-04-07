package com.ihomey.linkuphome.data.entity

import androidx.room.Embedded
import androidx.room.Relation


/**
 * Created by dongcaizheng on 2018/4/11.
 */
class RoomAndDevices{
    @Embedded
    var room: Room? = null

    @Relation(parentColumn ="id",entityColumn = "roomId")
    var devices: List<SingleDevice> = ArrayList()
}

