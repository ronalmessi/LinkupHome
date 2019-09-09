package com.ihomey.linkuphome.data.entity


import android.text.TextUtils
import android.util.Log
import androidx.room.*
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.annotations.SerializedName
import com.ihomey.linkuphome.data.db.DeviceStateValueConverter


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "device2")
data class Device(@PrimaryKey var id: String, @SerializedName("spaceId")var zoneId: Int, @SerializedName("groupId")var roomId: Int, var name: String,var type: Int, var instructId:Int, @TypeConverters(DeviceStateValueConverter::class) var parameters: DeviceState?): MultiItemEntity {

    @Ignore var hash: String=""

    @Ignore var macAddress: String=""


    override fun getItemType(): Int {
        return if(!TextUtils.equals("0",id)) 1 else -1
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Device) {
            return false
        }
        val singleDevice = other as Device?
        return  if(!TextUtils.isEmpty(singleDevice?.hash)){
            TextUtils.equals(this.hash, singleDevice?.hash)
        }else if(!TextUtils.equals("",singleDevice?.macAddress)){
            TextUtils.equals(this.macAddress, singleDevice?.macAddress)
        } else{
            TextUtils.equals(this.id, singleDevice?.id)
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + zoneId
        result = 31 * result + roomId
        result = 31 * result + name.hashCode()
        result = 31 * result + type
        result = 31 * result + instructId
        result = 31 * result + (parameters?.hashCode() ?: 0)
        result = 31 * result + hash.hashCode()
        result = 31 * result + macAddress.hashCode()
        return result
    }

    constructor(type: Int,name:String):this("0",0,0,name,type,0,DeviceState())

    constructor(type: Int,name:String,macAddress:String):this("0",0,0,name,type,0,DeviceState()){ this.macAddress=macAddress }
}

