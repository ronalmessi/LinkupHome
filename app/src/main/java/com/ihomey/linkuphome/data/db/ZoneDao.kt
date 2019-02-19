package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihomey.linkuphome.data.vo.Zone


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class ZoneDao {

    @Query("SELECT * FROM zone order by id asc")
    abstract fun getZones(): LiveData<List<Zone>>

    @Query("SELECT * FROM zone WHERE isCurrent =1")
    abstract fun getCurrentZone(): LiveData<Zone>

    @Query("DELETE FROM zone WHERE id = :id")
    abstract fun delete(id: Int)

    @Insert
    abstract fun insert(zone: Zone)

    @Query("UPDATE zone set name = :newName WHERE id = :id ")
    abstract fun updateZoneName(newName: String, id: Int)


    @Query("UPDATE zone set isCurrent =0 WHERE isCurrent =1 ")
    abstract fun deleteCurrentZone()


    @Query("UPDATE zone set isCurrent =1 WHERE id = :id ")
    abstract fun setCurrentZone(id: Int)


}