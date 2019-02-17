package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihomey.linkuphome.data.vo.SubZone
import com.ihomey.linkuphome.data.vo.Zone


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface SubZoneDao {

    @Query("SELECT * FROM sub_zone where parentId=:parentId order by id asc")
    fun getSubZones(parentId: Int): LiveData<List<SubZone>>

    @Query("DELETE FROM sub_zone WHERE id = :id")
    fun delete(id: Int)

    @Insert
    fun insert(subZone: SubZone)


}