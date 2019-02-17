package com.ihomey.linkuphome.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ihomey.linkuphome.data.vo.*

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Database(entities = [(LampCategory::class), (GroupDevice::class), (SingleDevice::class), (Model::class),(Zone::class),(SubZone::class)], version = 1, exportSchema = true)
abstract class LinkupHomeDb : RoomDatabase() {

    abstract fun lampCategoryDao(): LampCategoryDao
    abstract fun lampGroupDao(): GroupDeviceDao
    abstract fun lampDeviceDao(): SingleDeviceDao
    abstract fun  modelDao(): ModelDao
    abstract fun  zoneDao(): ZoneDao
    abstract fun  subZoneDao(): SubZoneDao

}