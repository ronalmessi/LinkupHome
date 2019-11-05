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
    abstract fun getZone(id: Int): LiveData<Zone>

    @Query("SELECT distinct id FROM zone WHERE active=1")
    abstract fun getCurrentZoneId(): LiveData<Int>

    @Query("SELECT min(id) FROM zone")
    abstract fun getMinZoneId(): Int

    @Query("UPDATE zone set active=0 where active=1")
    abstract fun resetAllActiveZone()

    @Query("DELETE FROM zone WHERE id = :id")
    abstract fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(zone: Zone): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(zones: List<Zone>)

    @Query("DELETE FROM zone")
    abstract fun deleteAll()

    @Update
    abstract fun update(zone: Zone)

    @Query("UPDATE zone set name = :newName WHERE id = :id ")
    abstract fun updateZoneName(newName: String, id: Int)

    @Query("UPDATE zone set active = 1 WHERE id = :id ")
    abstract fun activeZone(id: Int)

    @Query("UPDATE zone set nextDeviceIndex = :nextDeviceIndex WHERE id = :id ")
    abstract fun updateNextDeviceIndex(nextDeviceIndex: Int, id: Int)

    @Query("UPDATE zone set meshInfo = :meshInfo WHERE id = :id ")
    abstract fun updateMeshInfo(meshInfo: String, id: Int)

}