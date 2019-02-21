package com.ihomey.linkuphome.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ihomey.linkuphome.data.vo.ControlState

/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "room",primaryKeys =["id","zoneId"],foreignKeys =[(ForeignKey(entity = Zone::class, parentColumns = arrayOf("id"), childColumns = arrayOf("zoneId"), onDelete = ForeignKey.CASCADE))])
data class Room(val id: Int, val zoneId: Int, val name: String, val type: Int, var sendTypes: String?, @Embedded var state: ControlState){
    constructor(id: Int,zoneId:Int,name: String,type: Int):this(id,zoneId,name,type, "", ControlState())
}