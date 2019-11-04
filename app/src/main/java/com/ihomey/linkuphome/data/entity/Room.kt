package com.ihomey.linkuphome.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.ihomey.linkuphome.data.db.DeviceStateValueConverter

/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "room")
data class Room(@PrimaryKey val id: Int, @SerializedName("spaceId") val zoneId: Int, val name: String, val deviceTypes: String, val type: Int, var instructId: Int, @TypeConverters(DeviceStateValueConverter::class) var parameters: DeviceState?)