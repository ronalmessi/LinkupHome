package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.*
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

    @Query("SELECT distinct id FROM zone WHERE active=1")
    abstract fun getCurrentZoneId(): LiveData<Int>


    @Query("SELECT min(id) FROM zone")
    abstract fun getMinZoneId(): Int

    @Query("DELETE FROM zone WHERE id = :id")
    abstract fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(zone: Zone):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(zones: List<Zone>)

    @Update
    abstract fun update(zone: Zone)

    @Query("UPDATE zone set name = :newName WHERE id = :id ")
    abstract fun updateZoneName(newName: String, id: Int)



}