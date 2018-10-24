package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.ColumnInfo


/**
 * Created by dongcaizheng on 2018/4/11.
 */
data class Device(var name: String, var type: Int, @ColumnInfo(name = "macAddress") var macAddress: String="")