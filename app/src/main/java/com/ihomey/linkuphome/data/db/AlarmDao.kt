package com.ihomey.linkuphome.data.db


import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.ihomey.linkuphome.data.vo.Alarm
import com.ihomey.linkuphome.data.vo.Model


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm WHERE deviceId = :deviceId order by deviceId asc")
    fun getAlarms(deviceId: Int): LiveData<List<Alarm>>

    @Insert
    fun insert(alarm: Alarm)

    @Update
    fun update(alarm: Alarm)

    @Delete
    fun delete(alarm: Alarm)

}