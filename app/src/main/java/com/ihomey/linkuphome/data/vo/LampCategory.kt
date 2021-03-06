package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "category")
data class LampCategory(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int, @ColumnInfo(name = "type") var type: Int, @ColumnInfo(name = "added") var added: Int, @ColumnInfo(name = "networkKey") var networkKey: String, @ColumnInfo(name = "next_device_index") var nextDeviceIndex: Int, @ColumnInfo(name = "next_group_index") var nextGroupIndex: Int)