package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ihomey.linkuphome.data.entity.Zone


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class ZoneDao {

    @Query("SELECT * FROM zone order by id asc")
    abstract fun getZones(): LiveData<List<Zone>>

    @Query("SELECT * FROM zone WHERE id =:id")
    abstract fun getZone(id:Int): LiveData<Zone>


    @Query("SELECT min(id) FROM zone")
    abstract fun getMinZoneId(): Int

    @Query("DELETE FROM zone WHERE id = :id")
    abstract fun delete(id: Int)

    @Insert
    abstract fun insert(zone: Zone):Long

    @Update
    abstract fun update(zone: Zone)

    @Query("UPDATE zone set name = :newName WHERE id = :id ")
    abstract fun updateZoneName(newName: String, id: Int)



}