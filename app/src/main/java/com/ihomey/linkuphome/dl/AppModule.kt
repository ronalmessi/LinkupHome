package com.ihomey.linkuphome.dl

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ihomey.linkuphome.App
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.data.api.ApiService
import com.ihomey.linkuphome.data.api.LiveDataCallAdapterFactory
import com.ihomey.linkuphome.data.db.*
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class AppModule {


    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                .addNetworkInterceptor { chain ->
                    val request = chain.request().newBuilder()
                            .header("clientType", "1").build()
                    chain.proceed(request)
                }.build()
        return Retrofit.Builder()
                .baseUrl(AppConfig.API_SERVER)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build().create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(): LinkupHomeDb {
        return Room.databaseBuilder(App.instance, LinkupHomeDb::class.java, "LinkupHome.db").addMigrations(object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE device1 (id INTEGER not null,zoneId INTEGER not null,roomId INTEGER not null,name TEXT not null,type INTEGER not null,instructId INTEGER not null,parameters TEXT , PRIMARY KEY(id))")
                database.execSQL("CREATE TABLE room (id INTEGER not null,zoneId INTEGER not null,name TEXT not null,deviceTypes TEXT not null,type INTEGER not null,instructId INTEGER not null,parameters TEXT , PRIMARY KEY(id))")
                database.execSQL("CREATE TABLE zone (id INTEGER not null, name TEXT not null,netWorkKey TEXT not null,nextDeviceIndex INTEGER not null,nextGroupIndex INTEGER not null,active INTEGER not null, type INTEGER not null,PRIMARY KEY(id))")
                database.execSQL("CREATE TABLE local_state (id INTEGER not null,sceneMode INTEGER DEFAULT 0, openTimer INTEGER not null DEFAULT 0,closeTimer INTEGER not null DEFAULT 0,isOnOpenTimer INTEGER not null DEFAULT 0,isOnCloseTimer INTEGER not null DEFAULT 0, PRIMARY KEY(id))")

                database.execSQL("DROP TABLE device")
                database.execSQL("DROP TABLE group_device")
                database.execSQL("DROP TABLE category")
                database.execSQL("DROP TABLE model")
            }

        }).addMigrations(MIGRATION_2_3).build()
    }


    /**
     * 数据库版本 2->3
     */
    private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE device2 (id TEXT not null,zoneId INTEGER not null,roomId INTEGER not null,name TEXT not null,type INTEGER not null,instructId INTEGER not null,parameters TEXT , PRIMARY KEY(id))")
            database.execSQL("CREATE TABLE local_state1 (id TEXT not null,sceneMode INTEGER DEFAULT 0, openTimer INTEGER not null DEFAULT 0,closeTimer INTEGER not null DEFAULT 0,isOnOpenTimer INTEGER not null DEFAULT 0,isOnCloseTimer INTEGER not null DEFAULT 0,openDayOfWeek INTEGER not null DEFAULT 0,closeDayOfWeek INTEGER not null DEFAULT 0, PRIMARY KEY(id))")
            database.execSQL("CREATE TABLE alarm (id INTEGER not null,deviceId TEXT not null,dayOfWeek INTEGER not null DEFAULT 0, hour INTEGER not null DEFAULT 0,minute INTEGER not null DEFAULT 0,ringType INTEGER not null DEFAULT 1,type INTEGER not null DEFAULT 1,isOn INTEGER not null DEFAULT 0, PRIMARY KEY(id))")

            database.execSQL("DROP TABLE device1")
            database.execSQL("DROP TABLE local_state")
        }
    }

    @Singleton
    @Provides
    fun provideDeviceDao(db: LinkupHomeDb): DeviceDao {
        return db.deviceDao()
    }

    @Singleton
    @Provides
    fun provideRoom1Dao(db: LinkupHomeDb): RoomDao {
        return db.roomDao()
    }

    @Singleton
    @Provides
    fun provideZoneDao(db: LinkupHomeDb): ZoneDao {
        return db.zoneDao()
    }

    @Singleton
    @Provides
    fun provideLocalStateDao(db: LinkupHomeDb): LocalStateDao {
        return db.localStateDao()
    }

    @Singleton
    @Provides
    fun provideAlarmDao(db: LinkupHomeDb): AlarmDao {
        return db.alarmDao()
    }

}