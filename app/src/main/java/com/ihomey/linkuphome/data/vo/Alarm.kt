package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.chad.library.adapter.base.entity.MultiItemEntity


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "alarm")
data class Alarm(@PrimaryKey(autoGenerate = true) var id:Int=0, var deviceId: Int, var index: Int, var dayOfWeek: Int, var hour: Int, var minute: Int, var ringType: Int,var type: Int) : MultiItemEntity {
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