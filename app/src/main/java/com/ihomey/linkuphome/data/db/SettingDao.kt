package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ihomey.linkuphome.data.entity.Setting


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface SettingDao {

    @Query("SELECT * FROM setting")
    fun getSetting(): LiveData<Setting>

    @Insert
    fun insert(setting: Setting)


    @Update
    fun update(setting: Setting)


}