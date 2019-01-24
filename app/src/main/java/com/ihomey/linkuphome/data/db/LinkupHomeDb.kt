package com.ihomey.linkuphome.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Model
import com.ihomey.linkuphome.data.vo.SingleDevice

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Database(entities = [(LampCategory::class), (GroupDevice::class), (SingleDevice::class), (Model::class)], version = 2, exportSchema = true)
abstract class LinkupHomeDb : RoomDatabase() {

    abstract fun lampCategoryDao(): LampCategoryDao
    abstract fun lampGroupDao(): GroupDeviceDao
    abstract fun lampDeviceDao(): SingleDeviceDao
    abstract fun  modelDao(): ModelDao

}