package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.*
import com.ihomey.linkuphome.data.entity.Alarm


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm WHERE deviceId = :deviceId order by id asc")
    fun getAlarms(deviceId: Int): LiveData<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm)


    @Delete
    fun delete(alarm: Alarm)

}