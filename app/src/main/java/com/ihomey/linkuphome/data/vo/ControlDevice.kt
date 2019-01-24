package com.ihomey.linkuphome.data.vo

import androidx.room.Embedded

/**
 * Created by dongcaizheng on 2018/4/11.
 */
data class ControlDevice(val id: Int, @Embedded val device: Device, @Embedded val state:ControlState)