package com.ihomey.linkuphome.data.entity

import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.chad.library.adapter.base.entity.MultiItemEntity


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "alarm")
data class Alarm(@PrimaryKey var id:Int, @ColumnInfo(name = "deviceId")var deviceId: String, var dayOfWeek: Int, var hour: Int, var minute: Int, var ringType: Int=1, var type: Int=1, var isOn:Int) : MultiItemEntity {

    @Ignore var editMode:Int=0

    override fun getItemType(): Int {
        return if (id != -1) 1 else -1
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Alarm) {
            return false
        }
        if (other.id == this.id&& TextUtils.equals(this.deviceId, other.deviceId)&&other.dayOfWeek== this.dayOfWeek&&other.hour== this.hour&&other.minute== this.minute&&other.ringType== this.ringType&&other.type== this.type&&other.isOn== this.isOn) {
            return true
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + deviceId.hashCode()
        result = 31 * result + dayOfWeek
        result = 31 * result + hour
        result = 31 * result + minute
        result = 31 * result + ringType
        result = 31 * result + type
        result = 31 * result + isOn
        result = 31 * result + editMode
        return result
    }


}