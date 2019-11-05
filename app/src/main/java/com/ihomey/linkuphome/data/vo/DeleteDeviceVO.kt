package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid", "id", "meshInfo","timestamp")
data class DeleteDeviceVO(val guid: String, val id: String, val timestamp: Long,val meshInfo: String? = null,var signature: String? = null){
    override fun toString(): String {
        return if (meshInfo == null) {
            "guid$guid"+ "id$id" + "timestamp$timestamp"
        } else {
            "guid$guid"+ "id$id" + "meshInfo$meshInfo" + "timestamp$timestamp"
        }
    }
}