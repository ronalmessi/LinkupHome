package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "model", foreignKeys = [(ForeignKey(entity = SingleDevice::class, parentColumns = arrayOf("id"), childColumns = arrayOf("deviceId"), onDelete = ForeignKey.CASCADE)), (ForeignKey(entity = GroupDevice::class, parentColumns = arrayOf("id"), childColumns = arrayOf("groupId"), onDelete = ForeignKey.CASCADE))])
data class Model(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int?, @ColumnInfo(name = "deviceId") var deviceId: Int, @ColumnInfo(name = "groupId") var groupId: Int, @ColumnInfo(name = "groupIndex") var groupIndex: Int,@ColumnInfo(name = "type") var type: Int) {
    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is Model) {
            return false
        }
        val model = obj as Model?
        if (model!!.deviceId == this.deviceId && model.groupId == this.groupId && model.groupIndex == this.groupIndex&& model.type == this.type) {
            return true
        }
        return super.equals(obj)
    }
}