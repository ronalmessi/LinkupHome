package com.ihomey.linkuphome.data.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ihomey.linkuphome.data.entity.SingleDevice

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "model1")
data class Model1(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int, @ColumnInfo(name = "deviceId") var deviceId: Int, @ColumnInfo(name = "subZoneId") var subZoneId: Int,@ColumnInfo(name = "groupIndex") var groupIndex: Int,@ColumnInfo(name = "type") var type: Int) {
    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is Model1) {
            return false
        }
        val model = obj as Model1?
        if (model!!.deviceId == this.deviceId && model.subZoneId == this.subZoneId&& model.groupIndex == this.groupIndex&& model.type == this.type) {
            return true
        }
        return super.equals(obj)
    }
}