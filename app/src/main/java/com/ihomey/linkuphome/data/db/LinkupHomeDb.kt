package com.ihomey.linkuphome.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ihomey.linkuphome.data.entity.*

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Database(entities = [(Setting::class),(SingleDevice::class), (Zone::class), (Room::class), (Model::class)], version = 1, exportSchema = true)
abstract class LinkupHomeDb : RoomDatabase() {



    abstract fun settingDao(): SettingDao
    abstract fun singleDeviceDao(): SingleDeviceDao
    abstract fun roomDao(): RoomDao
    abstract fun zoneDao(): ZoneDao

    abstract fun modelDao(): ModelDao


}