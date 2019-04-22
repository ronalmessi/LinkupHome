package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.*
import com.ihomey.linkuphome.data.entity.LocalState


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class LocalStateDao {


    @Query("SELECT * FROM local_state WHERE id = :id")
    abstract fun getLocalState(id: Int): LiveData<LocalState>

    @Query("DELETE FROM local_state WHERE id = :id")
    abstract fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(localState: LocalState)
}