package com.ihomey.linkuphome.data.db


import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.ihomey.linkuphome.data.vo.Model


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface ModelDao {

    @Query("SELECT * FROM model WHERE deviceId = :deviceId order by groupIndex asc")
    fun getModels(deviceId: Int): LiveData<List<Model>>


    @Query("DELETE FROM model WHERE deviceId = :deviceId and groupId = :groupId and groupIndex = :groupIndex")
    fun deleteModel(deviceId: Int,groupId: Int,groupIndex:Int)

    @Insert
    fun insert(model: Model)

    @Insert
    fun insertModels(types: Array<Model>)

}