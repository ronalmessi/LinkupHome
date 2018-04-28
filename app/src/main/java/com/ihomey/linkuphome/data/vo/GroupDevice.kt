package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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