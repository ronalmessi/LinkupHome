package com.ihomey.linkuphome.dl

import androidx.room.Room
import androidx.room.RoomDatabase
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
import javax.inject.Singleton


@Module
class AppModule {


    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request().newBuilder()
                            .header("clientType", "1").build()
                    return chain.proceed(request)
                }
            }).build()
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
        return Room.databaseBuilder(App.instance, LinkupHomeDb::class.java, "LinkupHome.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("insert into setting(next_group_index,next_device_index) Values(1,32769)")
                    }
                })
                .build()
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
    fun provideModelDao(db: LinkupHomeDb): ModelDao {
        return db.modelDao()
    }

    @Singleton
    @Provides
    fun provideSettingDao(db: LinkupHomeDb): SettingDao {
        return db.settingDao()
    }

}