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

    @Query("SELECT * FROM model WHERE deviceId = :deviceId order by groupIndex asc")
    fun getModels(deviceId: Int): LiveData<List<Model>>


    @Query("DELETE FROM model WHERE deviceId = :deviceId and roomId = :groupId and groupIndex = :groupIndex")
    fun deleteModel(deviceId: Int,groupId: Int,groupIndex:Int)

    @Insert
    fun insert(model: Model)

    @Insert
    fun insertModels(types: Array<Model>)

}