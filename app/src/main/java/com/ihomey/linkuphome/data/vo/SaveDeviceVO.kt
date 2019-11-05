package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid", "meshInfo", "name", "pid", "spaceId", "timestamp", "type")
data class SaveDeviceVO(val guid: String, val name: String, val spaceId: Int, val timestamp: Long, val type: Int, var pid: Int? = null, var meshInfo: String? = null, var signature: String? = null) {
    override fun toString(): String {
        return if (pid == null && meshInfo == null) {
            "guid$guid"+ "name$name" + "spaceId$spaceId" + "timestamp$timestamp"+ "type$type"
        } else {
            "guid$guid" + "meshInfo$meshInfo" + "name$name" + "pid$pid"+ "spaceId$spaceId" + "timestamp$timestamp"+ "type$type"
        }
    }
}