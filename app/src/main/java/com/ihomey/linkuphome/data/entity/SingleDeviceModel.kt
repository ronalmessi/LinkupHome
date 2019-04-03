package com.ihomey.linkuphome.data.entity

import androidx.room.Embedded


/**
 * Created by dongcaizheng on 2018/4/11.
 */
data class SingleDeviceModel(@Embedded var singleDevice: SingleDevice, var groupInstructId: Int)

