package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.ihomey.linkuphome.data.entity.Zone


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class ZoneDao {

    @Query("SELECT * FROM zone2 order by id asc")
    abstract fun getZones(): LiveData<List<Zone>>

    @Transaction
    @Query("SELECT * FROM zone2 order by id asc")
    abstract fun getPagingZones(): DataSource.Factory<Int, Zone>

    @Query("SELECT * FROM zone2 WHERE id =:id")
    abstract fun getZone(id: Int): LiveData<Zone>

    @Query("SELECT distinct id FROM zone2 WHERE active=1")
    abstract fun getCurrentZoneId(): LiveData<Int>

    @Query("SELECT min(id) FROM zone2")
    abstract fun getMinZoneId(): Int

    @Query("UPDATE zone2 set active=0 where active=1")
    abstract fun resetAllActiveZone()

    @Query("DELETE FROM zone2 WHERE id = :id")
    abstract fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(zone: Zone): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(zones: List<Zone>)

    @Query("DELETE FROM zone2")
    abstract fun deleteAll()

    @Update
    abstract fun update(zone: Zone)

    @Query("UPDATE zone2 set name = :newName WHERE id = :id ")
    abstract fun updateZoneName(newName: String, id: Int)

    @Query("UPDATE zone2 set active = 1 WHERE id = :id ")
    abstract fun activeZone(id: Int)

    @Query("UPDATE zone2 set nextDeviceIndex = :nextDeviceIndex WHERE id = :id ")
    abstract fun updateNextDeviceIndex(nextDeviceIndex: Int, id: Int)

    @Query("UPDATE zone2 set meshInfo = :meshInfo WHERE id = :id ")
    abstract fun updateMeshInfo(meshInfo: String, id: Int)

}