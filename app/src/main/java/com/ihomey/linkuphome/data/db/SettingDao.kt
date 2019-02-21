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

    @Query("SELECT * FROM setting WHERE zoneId !=-1")
    fun getSetting(): LiveData<Setting>


//
//
//    @Query("DELETE FROM model WHERE deviceId = :deviceId and roomId = :groupId and groupIndex = :groupIndex")
//    fun deleteModel(deviceId: Int,groupId: Int,groupIndex:Int)
//
    @Insert
    fun insert(setting: Setting)

    @Query("UPDATE setting set zoneId =:zoneId")
    fun updateZoneId(zoneId: Int)


    @Update
    fun update(setting: Setting)



    @Query("UPDATE setting set next_device_index =:next_device_index")
    fun updateDeviceIndex(next_device_index: Int)
//
//    @Insert
//    fun insertModels(types: Array<Model>)

}