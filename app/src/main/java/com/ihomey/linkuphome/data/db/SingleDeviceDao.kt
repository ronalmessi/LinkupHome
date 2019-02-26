package com.ihomey.linkuphome.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.DeviceModel
import com.ihomey.linkuphome.getDistinct


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Dao
abstract class SingleDeviceDao {

    @Query("SELECT * FROM Device WHERE zoneId = :zoneId order by type asc,id asc")
    abstract fun getDevices(zoneId: Int): LiveData<List<SingleDevice>>

//    @Query("SELECT * FROM Device WHERE id in((SELECT deviceId FROM Model WHERE zoneId=:zoneId)) and zoneId = :zoneId ")
//    abstract fun getRoomDevices(zoneId: Int,deviceId:Int): LiveData<List<SingleDevice>>

    @Query("SELECT * FROM Device WHERE zoneId = :zoneId and type= :type order by id asc")
    abstract fun getDevices(zoneId: Int, type: Int): LiveData<List<SingleDevice>>

    @Query("SELECT * FROM Device order by type asc")
    abstract fun getDevices(): LiveData<List<SingleDevice>>

    @Query("SELECT * FROM Device WHERE id = :id and type=:deviceType")
    protected abstract fun getDevice(deviceType: Int, id: Int): LiveData<ControlDevice>

    fun getDeviceDistinctLiveData(deviceType: Int, id: Int): LiveData<ControlDevice> = getDevice(deviceType, id).getDistinct()

    @Query("SELECT * FROM Device WHERE id = :id and type=:deviceType")
    abstract fun getSingleDevice(deviceType: Int, id: Int): LiveData<SingleDevice>

    @Query("SELECT * FROM group_device WHERE type = :type union SELECT d.id,d.name,d.type,d.isOn,d.isLight,d.changeMode,d.colorPosition,d.colorTemperature,d.brightness,d.sceneMode,d.openTimer,d.closeTimer,d.isOnOpenTimer,d.isOnCloseTimer FROM Device AS d WHERE d.type = :type")
    abstract fun getControlDevices(type: Int): LiveData<List<ControlDevice>>

    @Query("SELECT * FROM Device WHERE id in (SELECT deviceId FROM Model WHERE zoneId = :groupId and type=:deviceType) and type=:deviceType")
    abstract fun getBondedDevices(deviceType: Int, groupId: Int): LiveData<List<SingleDevice>>

    @Query("SELECT * FROM Device WHERE id in (SELECT deviceId FROM Model WHERE roomId = :roomId and zoneId=:zoneId) and zoneId=:zoneId order by type asc")
    abstract fun getBindedDevices(zoneId: Int,roomId:Int): LiveData<List<SingleDevice>>


    @Query("SELECT * FROM Device WHERE id not in (SELECT deviceId FROM Model WHERE zoneId=:zoneId) and zoneId=:zoneId  order by type asc")
    abstract fun getUnBindedDevices(zoneId: Int): LiveData<List<SingleDevice>>


//    @Query("SELECT * FROM Device WHERE id not in (SELECT deviceId FROM Model WHERE roomId = :roomId and zoneId=:zoneId)  and zoneId=:zoneId  order by type asc")
//    abstract fun getUnBindedDevices(zoneId: Int,roomId:Int): LiveData<List<SingleDevice>>

    @Query("SELECT * FROM Device WHERE id not in (SELECT deviceId FROM Model WHERE zoneId = :groupId and type=:deviceType) and type=:deviceType")
    abstract fun getUnBondedDevices(deviceType: Int, groupId: Int): LiveData<List<SingleDevice>>

    @Query("SELECT * FROM Device WHERE id not in (SELECT deviceId FROM Model1 WHERE subZOneId = :subZOneId)")
    abstract fun getUnBondedDevices(subZOneId: Int): LiveData<List<SingleDevice>>

    @Insert
    abstract fun insert(singleDevice: SingleDevice)

    @Delete
    abstract fun delete(singleDevice: SingleDevice)

    @Query("DELETE FROM Device WHERE id = :id")
    abstract fun deleteById( id: Int)

    @Query("DELETE FROM Device WHERE type = :type")
    abstract fun deleteByType(type: Int)

    @Query("UPDATE Device set name = :newName WHERE id = :id and type=:deviceType")
    abstract fun updateDeviceName(deviceType: Int, newName: String, id: Int)

    @Query("UPDATE Device set name = :newName WHERE id = :id")
    abstract fun updateDeviceName(newName: String, id: Int)

    @Query("UPDATE Device set sceneMode = :sceneMode WHERE id = :deviceId and type=:deviceType")
    abstract fun updateDeviceSceneMode(deviceType: Int, deviceId: Int, sceneMode: Int)

    @Query("SELECT * FROM Device WHERE type = :type")
    abstract fun getDeviceModels(type: Int): LiveData<List<DeviceModel>>


    @Insert
    abstract fun insertDevices(types: Array<SingleDevice>)

    @Update
    abstract fun update(singleDevice: SingleDevice)


    @Query("UPDATE Device set isOn=:on , isLight=:light,changeMode=:changeMode,colorPosition=:colorPosition,colorTemperature=:colorTemperature,brightness=:brightness,sceneMode=:sceneMode,openTimer=:openTimer,closeTimer=:closeTimer,isOnOpenTimer=:openTimerOn,isOnCloseTimer=:closeTimerOn WHERE id = :singleDeviceId")
    abstract fun updateDeviceState(singleDeviceId: Int, on: Int, light: Int, changeMode: Int, colorPosition: Float, colorTemperature: Int, brightness: Int, sceneMode: Int, openTimer: Long, closeTimer: Long, openTimerOn: Int, closeTimerOn: Int)


    @Query("UPDATE Device set isOn=:on  WHERE id in (SELECT deviceId FROM Model WHERE zoneId=:zoneId and roomId=:roomId) and zoneId=:zoneId")
    abstract fun updateDeviceState(zoneId:Int,roomId: Int, on: Int)
}