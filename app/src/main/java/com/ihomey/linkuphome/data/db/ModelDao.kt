package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihomey.linkuphome.data.entity.Model


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface ModelDao {

    @Query("SELECT * FROM model WHERE deviceId = :deviceId and zoneId=:zoneId")
    fun getModels(deviceId: Int,zoneId:Int): LiveData<List<Model>>

//    @Query("SELECT * FROM model1 WHERE subZoneId = :subZoneId")
//    fun getModelsBySubZoneId(subZoneId: Int): List<Model1>

    @Query("DELETE FROM model WHERE deviceId = :deviceId and roomId = :roomId and zoneId = :zoneId")
    fun deleteModel(deviceId: Int, roomId: Int, zoneId: Int)

    @Insert
    fun insert(model: Model)

    @Query("update model set type=(select type from device where id=:deviceId  and zoneId=:zoneId) where deviceId=:deviceId and roomId=:roomId and zoneId=:zoneId")
    fun updateType(deviceId: Int, roomId: Int, zoneId: Int)


}