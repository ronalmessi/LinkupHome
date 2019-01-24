package com.ihomey.linkuphome.data.db


import androidx.lifecycle.LiveData
import androidx.room.*
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.getDistinct


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class GroupDeviceDao {

    @Query("SELECT * FROM group_device WHERE type = :type")
    abstract fun getGroups(type: Int): LiveData<List<GroupDevice>>


    @Query("SELECT * FROM group_device WHERE id = :id and type=:deviceType")
    protected abstract fun getGroup(deviceType: Int, id: Int): LiveData<ControlDevice>

    fun getGroupDistinctLiveData(deviceType: Int, id: Int): LiveData<ControlDevice> = getGroup(deviceType, id).getDistinct()


    @Delete
    abstract fun delete(lampGroup: GroupDevice)

    @Query("DELETE FROM group_device WHERE type = :type")
    abstract fun deleteByType(type: Int)

    @Update
    abstract fun update(lampGroup: GroupDevice)

    @Insert
    abstract fun insert(lampGroup: GroupDevice)

    @Insert
    abstract  fun insertGroups(types: Array<GroupDevice>)

    @Query("UPDATE group_device set name = :newName WHERE id = :id and type=:deviceType")
    abstract fun updateGroupName(deviceType: Int, newName: String, id: Int)


    @Query("UPDATE group_device set sceneMode = :sceneMode WHERE id = :deviceId and type=:deviceType")
    abstract  fun updateDeviceSceneMode(deviceType: Int, deviceId: Int, sceneMode: Int)

}