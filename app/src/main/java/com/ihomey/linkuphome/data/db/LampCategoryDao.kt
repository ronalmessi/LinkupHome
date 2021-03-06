package com.ihomey.linkuphome.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.ihomey.linkuphome.data.vo.LampCategory


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
interface LampCategoryDao {

    @Query("SELECT * FROM Category WHERE added = :isAdded and type!=-1")
    fun getCategories(isAdded: Int): LiveData<List<LampCategory>>

    @Query("SELECT * FROM Category WHERE type in (:types) order by id")
    fun getSettings(types: Array<Int>): LiveData<List<LampCategory>>

    @Update
    fun updateCategory(lampCategory: LampCategory)

    @Query("UPDATE Category set next_group_index= :groupIndex WHERE type=:type")
    fun updateGroupIndex(groupIndex: Int, type: Int)

    @Query("UPDATE Category set networkKey= :networkKey ,next_group_index=1 WHERE type= :type")
    fun updateNetWorkkey(networkKey: String, type: Int)
}