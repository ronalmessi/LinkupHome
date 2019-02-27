package com.ihomey.linkuphome.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "setting")
data class Setting(@PrimaryKey(autoGenerate = true) val id: Int, @ColumnInfo(name = "next_device_index") var nextDeviceIndex: Int, @ColumnInfo(name = "next_group_index") var nextGroupIndex: Int)