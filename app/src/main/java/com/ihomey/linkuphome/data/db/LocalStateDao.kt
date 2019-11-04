package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ihomey.linkuphome.data.entity.LocalState


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class LocalStateDao {


    @Query("SELECT * FROM local_state1 WHERE id = :id")
    abstract fun getLocalState(id: String): LiveData<LocalState>

    @Query("DELETE FROM local_state1 WHERE id = :id")
    abstract fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(localState: LocalState)
}