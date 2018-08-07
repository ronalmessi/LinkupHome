package com.ihomey.linkuphome.module

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.ihomey.linkuphome.App
import com.ihomey.linkuphome.data.db.*
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton


/**
 * Created by dongcaizheng on 2018/1/11.
 */
@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideDb(): LinkupHomeDb {
        return Room.databaseBuilder(App.instance, LinkupHomeDb::class.java, "LinkupHome.db").addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val uniqueID = UUID.randomUUID()
                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(-1,0,'',1,32769)")
                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(0,0,'$uniqueID',1,-1)")
                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(1,0,'$uniqueID',1,-1)")
                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(2,0,'$uniqueID',1,-1)")
                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(3,0,'$uniqueID',1,-1)")
//                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(4,0,'$uniqueID',1,-1)")
            }
        }).build()
    }

    @Singleton
    @Provides
    fun provideLampCategoryDao(db: LinkupHomeDb): LampCategoryDao {
        return db.lampCategoryDao()
    }

    @Singleton
    @Provides
    fun provideLampGroupDao(db: LinkupHomeDb): GroupDeviceDao {
        return db.lampGroupDao()
    }

    @Singleton
    @Provides
    fun provideLampDeviceDao(db: LinkupHomeDb): SingleDeviceDao {
        return db.lampDeviceDao()
    }

    @Singleton
    @Provides
    fun provideModelDao(db: LinkupHomeDb): ModelDao {
        return db.modelDao()
    }

}