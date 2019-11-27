package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */

@JsonPropertyOrder("act", "deviceId", "groupId", "guid", "spaceId", "timestamp")
data class BindDeviceVO(val guid: String, val spaceId: Int, val groupId: Int, val deviceId: String, val act: String, val timestamp: Long, var signature: String? = null){

    override fun toString(): String {
        val sb=StringBuilder()
        sb.append("act$act").append("deviceId$deviceId").append("groupId$groupId").append("guid$guid").append("spaceId$spaceId").append("timestamp$timestamp")
        return sb.toString()
    }
}