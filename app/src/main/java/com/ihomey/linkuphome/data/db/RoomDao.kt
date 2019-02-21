package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.vo.SubZoneModel


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface RoomDao {

    @Query("SELECT * FROM room where zoneId=:parentId order by id asc")
    fun getSubZones(parentId: Int): LiveData<List<Room>>


    @Query("SELECT * FROM room where zoneId=:parentId order by id asc")
    fun getSubZoneModels(parentId: Int): LiveData<List<SubZoneModel>>


    @Query("SELECT * FROM room where zoneId=:zoneId order by id asc")
    fun getRooms(zoneId: Int): LiveData<List<Room>>

    @Query("DELETE FROM room WHERE id = :id")
    fun delete(id: Int)

    @Insert
    fun insert(subZone: Room)

    @Query("update room set sendTypes=(select group_concat(distinct type) from model where roomId=:roomId and zoneId=:zoneId and type!=-1 group by roomId ,zoneId) where id=:roomId and zoneId=:zoneId")
    fun updateSendTypes(roomId: Int, zoneId: Int)

    @Query("SELECT * FROM room where id=:id")
    fun getSubZone(id: Int): Room

    @Query("UPDATE room set isOn=:on , isLight=:light,changeMode=:changeMode,colorPosition=:colorPosition,colorTemperature=:colorTemperature,brightness=:brightness,sceneMode=:sceneMode,openTimer=:openTimer,closeTimer=:closeTimer,isOnOpenTimer=:openTimerOn,isOnCloseTimer=:closeTimerOn WHERE id = :subZoneId")
    fun updateSubZoneState(subZoneId: Int, on: Int, light: Int, changeMode: Int, colorPosition: Float, colorTemperature: Int, brightness: Int, sceneMode: Int, openTimer: Long, closeTimer: Long, openTimerOn: Int, closeTimerOn: Int)


    @Query("UPDATE room set name = :newName WHERE id = :id ")
    fun updateRoomName(newName: String, id: Int)
}