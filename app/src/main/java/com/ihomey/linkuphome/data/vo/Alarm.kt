package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.chad.library.adapter.base.entity.MultiItemEntity


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "alarm",foreignKeys = [(ForeignKey(entity = SingleDevice::class, parentColumns = arrayOf("id"), childColumns = arrayOf("deviceId"), onDelete = ForeignKey.CASCADE))])
data class Alarm(@PrimaryKey var id:Int, @ColumnInfo(name = "deviceId")var deviceId: Int,var dayOfWeek: Int, var hour: Int, var minute: Int, var ringType: Int=1, var type: Int=1, var isOn:Int) : MultiItemEntity {
    override fun getItemType(): Int {
        return if (id != -1) 1 else -1
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is Alarm) {
            return false
        }
        val alarm = obj
        if (alarm.id == this.id) {
            return true
        }
        return super.equals(obj)
    }
}