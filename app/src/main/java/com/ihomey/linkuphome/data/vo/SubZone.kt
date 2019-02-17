package com.ihomey.linkuphome.data.vo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "sub_zone", foreignKeys = [(ForeignKey(entity = Zone::class, parentColumns = arrayOf("id"), childColumns = arrayOf("parentId"), onDelete = ForeignKey.CASCADE))])
data class SubZone(@PrimaryKey(autoGenerate = true) val id: Int, var name: String, val type: Int, val parentId: Int, var sendTypes: String){

    constructor(name: String,type: Int,parentId: Int):this(0,name,type,parentId,"")
}