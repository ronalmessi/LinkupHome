package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid", "meshInfo", "name", "timestamp")
data class CreateZoneVO(val guid: String, val meshInfo:String,val name: String, val timestamp: Long, var signature: String? = null){

    override fun toString(): String {
        return "guid$guid"+"meshInfo$meshInfo"+"name$name"+"timestamp$timestamp"
    }
}