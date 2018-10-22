package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.ColumnInfo


/**
 * Created by dongcaizheng on 2018/4/11.
 */
data class ControlState(@ColumnInfo(name = "isOn") var on: Int=1, @ColumnInfo(name = "isLight") var light: Int = 0, var changeMode: Int = -1, var colorPosition: Float = 0f, var colorTemperature: Int = 3000, var brightness: Int = 20, var sceneMode: Int = 0, var openTimer: Long = 0, var closeTimer: Long = 0, @ColumnInfo(name = "isOnOpenTimer") var openTimerOn: Int = 0, @ColumnInfo(name = "isOnCloseTimer") var closeTimerOn: Int = 0,var openDayOfWeek: Int=0,var closeDayOfWeek: Int=0)

