package com.ihomey.linkuphome.listener

/**
 * Created by Administrator on 2017/6/21.
 */
interface GroupUpdateListener {
    fun groupsUpdated(deviceId: Int,groupId: Int,groupIndex: Int,success: Boolean, msg: String?)
}