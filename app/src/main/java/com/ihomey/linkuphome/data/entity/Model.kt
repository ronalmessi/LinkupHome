package com.ihomey.linkuphome.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "model")
data class Model(@PrimaryKey(autoGenerate = true) var id: Int, @SerializedName("spaceId") var zoneId: Int, var deviceInstructId: Int,  var groupInstructId: Int, @SerializedName("index") var groupIndex: Int)