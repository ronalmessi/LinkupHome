package com.ihomey.linkuphome.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "local_state")
data class LocalState(@PrimaryKey var id: String,var sceneMode: Int? = 0, var openTimer: Long = 0, var closeTimer: Long = 0, @ColumnInfo(name = "isOnOpenTimer") var openTimerOn: Int = 0, @ColumnInfo(name = "isOnCloseTimer") var closeTimerOn: Int = 0,var openDayOfWeek: Int=0,var closeDayOfWeek: Int=0)

