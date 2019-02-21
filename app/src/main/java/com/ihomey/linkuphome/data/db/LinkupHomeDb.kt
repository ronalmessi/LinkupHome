package com.ihomey.linkuphome.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Model1

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Database(entities = [(Setting::class),(SingleDevice::class), (Zone::class), (Room::class), (Model::class),(Model1::class),(GroupDevice::class),(LampCategory::class)], version = 1, exportSchema = true)
abstract class LinkupHomeDb : RoomDatabase() {



    abstract fun settingDao(): SettingDao
    abstract fun singleDeviceDao(): SingleDeviceDao
    abstract fun modelDao(): ModelDao
    abstract fun roomDao(): RoomDao
    abstract fun zoneDao(): ZoneDao



    abstract fun model1Dao(): Model1Dao
    abstract fun lampCategoryDao(): LampCategoryDao
    abstract fun groupDeviceDao(): GroupDeviceDao


}