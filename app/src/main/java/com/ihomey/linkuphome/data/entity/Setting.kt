package com.ihomey.linkuphome.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "setting",foreignKeys =[(ForeignKey(entity = Zone::class, parentColumns = arrayOf("id"), childColumns = arrayOf("zoneId"), onDelete = ForeignKey.SET_NULL,onUpdate =ForeignKey.CASCADE ))])
data class Setting(@PrimaryKey(autoGenerate = true) val id: Int, var zoneId: Int=-1, @ColumnInfo(name = "next_device_index") var nextDeviceIndex: Int, @ColumnInfo(name = "next_group_index") var nextGroupIndex: Int) {
    constructor(zoneId: Int) : this(0, zoneId, 32769, 1)
}