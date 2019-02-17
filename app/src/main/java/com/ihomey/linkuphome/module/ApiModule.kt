package com.ihomey.linkuphome.module

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ihomey.linkuphome.App
import com.ihomey.linkuphome.data.db.*
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton
import androidx.room.migration.Migration


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
                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(5,0,'$uniqueID',1,-1)")
                db.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) Values(6,0,'$uniqueID',1,-1)")
            }
        }).addMigrations(MIGRATION_1_2).build()
    }

    private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) select type,added,networkKey,next_group_index,next_device_index from category where id=5")
            database.execSQL("insert into category(type,added,networkKey,next_group_index,next_device_index) select type,added,networkKey,next_group_index,next_device_index from category where id=5")
            database.execSQL("update category set type=5 where id=6")
            database.execSQL("update category set type=6 where id=7")
        }
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

    @Singleton
    @Provides
    fun provideZoneDao(db: LinkupHomeDb): ZoneDao {
        return db.zoneDao()
    }

    @Singleton
    @Provides
    fun provideSubZoneDao(db: LinkupHomeDb): SubZoneDao {
        return db.subZoneDao()
    }

}