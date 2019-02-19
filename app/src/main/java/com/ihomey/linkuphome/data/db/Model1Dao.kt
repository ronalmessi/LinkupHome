package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihomey.linkuphome.data.vo.Model1


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface Model1Dao {

    @Query("SELECT * FROM model1 WHERE deviceId = :deviceId")
    fun getModels(deviceId: Int): LiveData<List<Model1>>

    @Query("SELECT * FROM model1 WHERE subZoneId = :subZoneId")
    fun getModelsBySubZoneId(subZoneId: Int): List<Model1>

    @Query("DELETE FROM model1 WHERE deviceId = :deviceId and subZOneId = :subZOneId and groupIndex = :groupIndex")
    fun deleteModel(deviceId: Int, subZOneId: Int,groupIndex:Int)

    @Insert
    fun insert(model: Model1)

    @Query("update model1 set type=(select type from device where id=:deviceId) where deviceId=:deviceId and subZoneId=:subZoneId")
    fun updateType(deviceId: Int, subZoneId: Int)

    @Insert
    fun insertModels(types: Array<Model1>)

}