package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.ihomey.linkuphome.data.entity.DeviceState
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.RoomAndDevices


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface RoomDao {

    @Query("DELETE FROM room WHERE id = :id")
    fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(room: Room)

    @Query("SELECT * FROM room where id=:roomId ")
    fun getRoom(roomId: Int): Room?

    @Transaction
    @Query("SELECT * FROM room where zoneId=:zoneId order by id asc")
    fun getRooms(zoneId: Int): LiveData<List<RoomAndDevices>>

    @Transaction
    @Query("SELECT * FROM room where zoneId=:zoneId order by id asc")
    fun getPagingRooms(zoneId: Int): DataSource.Factory<Int, RoomAndDevices>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(rooms: List<Room>)

    @Query("DELETE FROM room WHERE zoneId = :zoneId")
    fun deleteAll(zoneId: Int)

    @Query("UPDATE room set parameters= :deviceState WHERE id = :roomId")
    fun updateState(roomId: Int, deviceState: DeviceState)

    @Query("UPDATE room set name= :newName WHERE id = :roomId")
    fun updateName(roomId: Int, newName: String)

}