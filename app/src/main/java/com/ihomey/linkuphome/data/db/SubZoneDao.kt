package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihomey.linkuphome.data.vo.SubZone
import com.ihomey.linkuphome.data.vo.SubZoneModel
import com.ihomey.linkuphome.data.vo.Zone


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface SubZoneDao {

    @Query("SELECT * FROM sub_zone where parentId=:parentId order by id asc")
    fun getSubZones(parentId: Int): LiveData<List<SubZone>>


    @Query("SELECT * FROM sub_zone where parentId=:parentId order by id asc")
    fun getSubZoneModels(parentId: Int): LiveData<List<SubZoneModel>>

    @Query("DELETE FROM sub_zone WHERE id = :id")
    fun delete(id: Int)

    @Insert
    fun insert(subZone: SubZone)

    @Query("update sub_zone set sendTypes=(select group_concat(distinct type) from model1 where subZoneId=:subZoneId and type!=-1 group by subZoneId) where id=:subZoneId")
    fun updateSendTypes(subZoneId: Int)

    @Query("SELECT * FROM sub_zone where id=:id")
    fun getSubZone(id: Int): SubZone

    @Query("UPDATE sub_zone set isOn=:on , isLight=:light,changeMode=:changeMode,colorPosition=:colorPosition,colorTemperature=:colorTemperature,brightness=:brightness,sceneMode=:sceneMode,openTimer=:openTimer,closeTimer=:closeTimer,isOnOpenTimer=:openTimerOn,isOnCloseTimer=:closeTimerOn WHERE id = :subZoneId")
    fun updateSubZoneState(subZoneId: Int, on: Int, light: Int, changeMode: Int, colorPosition: Float, colorTemperature: Int, brightness: Int, sceneMode: Int, openTimer: Long, closeTimer: Long, openTimerOn: Int, closeTimerOn: Int)


    @Query("UPDATE sub_zone set name = :newName WHERE id = :id ")
    fun updateSubZoneName(newName: String, id: Int)
}