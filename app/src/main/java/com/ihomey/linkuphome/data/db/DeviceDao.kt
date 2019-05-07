package com.ihomey.linkuphome.data.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.ihomey.linkuphome.data.entity.DeviceState
import com.ihomey.linkuphome.data.entity.Device


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class DeviceDao {

    @Query("SELECT * FROM device1 WHERE zoneId = :zoneId order by type asc,id asc")
    abstract fun getDevices(zoneId: Int): LiveData<List<Device>>

    @Transaction
    @Query("SELECT * FROM device1 WHERE zoneId = :zoneId order by type asc,id asc")
    abstract fun getPagingDevices(zoneId: Int):DataSource.Factory<Int, Device>

    @Transaction
    @Query("SELECT * FROM device1 WHERE zoneId = :zoneId and type =:type order by id asc")
    abstract fun getDevicesByType(zoneId: Int,type:Int):LiveData<List<Device>>

    @Transaction
    @Query("SELECT * FROM device1 WHERE zoneId = :zoneId and roomId =0 order by type asc,id asc")
    abstract fun getPagingUnBondedDevices(zoneId: Int): DataSource.Factory<Int, Device>

    @Transaction
    @Query("SELECT * FROM device1 WHERE zoneId = :zoneId and roomId = :roomId order by type asc,id asc")
    abstract fun getPagingBondedDevices(zoneId: Int,roomId: Int): DataSource.Factory<Int, Device>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(devices: List<Device>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(device: Device)

    @Delete
    abstract fun delete(device: Device)

    @Query("DELETE FROM device1 WHERE id = :id")
    abstract fun delete(id: Int)

    @Query("DELETE FROM device1 WHERE zoneId = :zoneId")
    abstract fun deleteAll(zoneId: Int)

    @Query("UPDATE device1 set roomId = 0 WHERE roomId = :roomId")
    abstract fun unBondFromRoom(roomId: Int)

    @Query("UPDATE device1 set roomId = 0 WHERE instructId = :deviceInstructId and zoneId= :zoneId")
    abstract fun unBondDeviceFromRoom(deviceInstructId:Int,zoneId:Int)

    @Query("UPDATE device1 set roomId = :roomId WHERE instructId = :deviceInstructId and zoneId= :zoneId")
    abstract fun bondToRoom(roomId: Int,deviceInstructId:Int,zoneId: Int)


    @Query("SELECT * FROM device1 WHERE zoneId = :zoneId and roomId = :roomId")
    abstract fun getDevices(zoneId: Int,roomId:Int): List<Device>

    @Query("UPDATE device1 set parameters= :deviceState WHERE roomId = :roomId")
    abstract fun updateStateByRoomId(roomId: Int,deviceState:DeviceState)


    @Query("UPDATE device1 set parameters= :deviceState WHERE id = :deviceId")
    abstract fun updateState(deviceId: Int,deviceState:DeviceState)




}