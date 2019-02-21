package com.ihomey.linkuphome.module

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ihomey.linkuphome.App
import com.ihomey.linkuphome.data.db.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by dongcaizheng on 2018/1/11.
 */
@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideDb(): LinkupHomeDb {
        return Room.databaseBuilder(App.instance, LinkupHomeDb::class.java, "LinkupHome.db")
               .build()

//                .addCallback(object : RoomDatabase.Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        db.execSQL("insert into setting(next_group_index,next_device_index) Values(1,32769)")
//                    }
//                })
    }

    @Singleton
    @Provides
    fun provideModelDao(db: LinkupHomeDb): ModelDao {
        return db.modelDao()
    }

    @Singleton
    @Provides
    fun provideSingleDeviceDao(db: LinkupHomeDb): SingleDeviceDao {
        return db.singleDeviceDao()
    }

    @Singleton
    @Provides
    fun provideZoneDao(db: LinkupHomeDb): ZoneDao {
        return db.zoneDao()
    }

    @Singleton
    @Provides
    fun provideRoomDao(db: LinkupHomeDb): RoomDao {
        return db.roomDao()
    }


    @Singleton
    @Provides
    fun provideLampCategoryDao(db: LinkupHomeDb): LampCategoryDao {
        return db.lampCategoryDao()
    }

    @Singleton
    @Provides
    fun provideGroupDeviceDao(db: LinkupHomeDb): GroupDeviceDao {
        return db.groupDeviceDao()
    }

    @Singleton
    @Provides
    fun provideModel1Dao(db: LinkupHomeDb): Model1Dao {
        return db.model1Dao()
    }

    @Singleton
    @Provides
    fun provideSettingDao(db: LinkupHomeDb): SettingDao {
        return db.settingDao()
    }

}