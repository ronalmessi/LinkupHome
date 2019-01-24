package com.ihomey.linkuphome.data.vo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by dongcaizheng on 2018/4/11.
 */
@Entity(tableName = "group_device")
data class GroupDevice(@PrimaryKey var id: Int, @Embedded var device: Device?, @Embedded var state: ControlState?) : MultiItemEntity {
    override fun getItemType(): Int {
        return if (id != -1) 1 else -1
    }
}