package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid", "timestamp")
data class BaseVO(val guid: String, val timestamp: Long, var signature: String? = null){
    override fun toString(): String {
        return "guid$guid"+"timestamp$timestamp"
    }
}