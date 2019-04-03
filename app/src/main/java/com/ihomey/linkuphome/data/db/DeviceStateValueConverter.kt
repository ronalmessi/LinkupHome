package com.ihomey.linkuphome.data.db


import androidx.room.TypeConverter
import com.fasterxml.jackson.databind.ObjectMapper

import com.ihomey.linkuphome.data.entity.DeviceState



public class DeviceStateValueConverter {

    @TypeConverter
    public fun DeviceStateValueConverter(deviceState: DeviceState?): String? {
        if (deviceState == null) {
            return null
        }
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(deviceState)
    }

    @TypeConverter
    public fun DeviceStateValueConverter(deviceStateValues: String?): DeviceState? {
        if (deviceStateValues == null) {
            return null
        }
        val mapper = ObjectMapper()
        return mapper.readValue(deviceStateValues, DeviceState::class.java)
    }
}