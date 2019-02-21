package com.ihomey.linkuphome.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "zone")
data class Zone(@PrimaryKey(autoGenerate = true) val id: Int, var name: String, var isCurrent: Boolean, val networkKey: String){
    constructor(name: String,isCurrent: Boolean):this(0,name,isCurrent, UUID.randomUUID().toString())
}