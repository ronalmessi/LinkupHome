package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.entity.ZoneSetting


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class ZoneDao {

    @Query("SELECT * FROM zone order by id asc")
    abstract fun getZones(): LiveData<List<Zone>>

    @Query("SELECT * FROM zone WHERE id =:id")
    abstract fun getZone(id:Int): LiveData<Zone>


//
//    @Query("SELECT zone.*,setting.* FROM zone inner join setting on zone.id =setting.zoneId WHERE zone.isCurrent =1")
//    abstract fun getCurrentZone(): LiveData<ZoneSetting>

    @Query("DELETE FROM zone WHERE id = :id")
    abstract fun delete(id: Int)

    @Insert
    abstract fun insert(zone: Zone):Long

    @Update
    abstract fun update(zone: Zone)

    @Query("UPDATE zone set name = :newName WHERE id = :id ")
    abstract fun updateZoneName(newName: String, id: Int)


//    @Query("UPDATE zone set isCurrent =0 WHERE isCurrent =1 ")
//    abstract fun deleteCurrentZone()
//
//
//    @Query("UPDATE zone set isCurrent =1 WHERE id = :id ")
//    abstract fun setCurrentZone(id: Int)


}