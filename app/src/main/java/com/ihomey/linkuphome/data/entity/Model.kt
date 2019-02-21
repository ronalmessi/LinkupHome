package com.ihomey.linkuphome.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "model", foreignKeys = [(ForeignKey(entity = SingleDevice::class, parentColumns = arrayOf("id","zoneId"), childColumns = arrayOf("deviceId","zoneId"), onDelete = ForeignKey.CASCADE)), (ForeignKey(entity = Room::class, parentColumns = arrayOf("id","zoneId"), childColumns = arrayOf("roomId","zoneId"), onDelete = ForeignKey.CASCADE))])
data class Model(@PrimaryKey(autoGenerate = true) var id: Int,var zoneId: Int,var deviceId: Int, var roomId: Int,var groupIndex: Int,var type: Int)