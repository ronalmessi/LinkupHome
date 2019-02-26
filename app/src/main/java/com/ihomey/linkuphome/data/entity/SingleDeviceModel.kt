package com.ihomey.linkuphome.data.entity

import androidx.room.Embedded

data class SingleDeviceModel(@Embedded val device: SingleDevice, @Embedded val model: Model)