package com.ihomey.linkuphome.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ihomey.linkuphome.data.entity.*

/**
 * Created by dongcaizheng on 2018/4/9.
 */

@TypeConverters(DeviceStateValueConverter::class)
@Database(entities = [(SingleDevice::class),(Room::class), (Zone::class),(LocalState::class)], version = 2, exportSchema = true)
abstract class LinkupHomeDb : RoomDatabase() {
    abstract fun singleDeviceDao(): SingleDeviceDao
    abstract fun roomDao(): RoomDao
    abstract fun zoneDao(): ZoneDao
    abstract fun localStateDao(): LocalStateDao
}