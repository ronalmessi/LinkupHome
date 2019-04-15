package com.ihomey.linkuphome.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "zone")
data class Zone(@PrimaryKey val id: Int, var name: String, val netWorkKey: String, val nextDeviceIndex: Int, val nextGroupIndex: Int,val active:Int,val type:Int)