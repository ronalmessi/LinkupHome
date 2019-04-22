package com.ihomey.linkuphome.data.entity


import androidx.room.*
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.annotations.SerializedName
import com.ihomey.linkuphome.data.db.DeviceStateValueConverter


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "device1")
data class Device(@PrimaryKey var id: Int, @SerializedName("spaceId")var zoneId: Int, @SerializedName("groupId")var roomId: Int, var name: String, var type: Int, var instructId:Int, @TypeConverters(DeviceStateValueConverter::class) var parameters: DeviceState?): MultiItemEntity {

    @Ignore var hash: Int=0

    override fun getItemType(): Int {
        return if (id != 0) 1 else -1
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is Device) {
            return false
        }
        val singleDevice = obj as Device?
        return if(singleDevice?.hash==0){
            singleDevice.id == this.id
        }else{
            singleDevice?.hash == this.hash
        }
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + zoneId
        result = 31 * result + name.hashCode()
        result = 31 * result + type
        result = 31 * result + instructId
        result = 31 * result + parameters.hashCode()
        return result
    }


    constructor(type: Int,name:String):this(0,0,0,name,type,0,DeviceState())

}

