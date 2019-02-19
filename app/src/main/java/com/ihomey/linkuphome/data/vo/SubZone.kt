package com.ihomey.linkuphome.data.vo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "sub_zone", foreignKeys = [(ForeignKey(entity = Zone::class, parentColumns = arrayOf("id"), childColumns = arrayOf("parentId"), onDelete = ForeignKey.CASCADE))])
data class SubZone(@PrimaryKey val id: Int, @Embedded var device: Device, val parentId: Int, var sendTypes: String?,@Embedded var state: ControlState?)