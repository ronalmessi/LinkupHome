package com.ihomey.linkuphome.data.vo


/**
 * Created by dongcaizheng on 2018/4/11.
 */
data class SaveDeviceVO(val guid: String, val id: String?=null,val name: String, val spaceId: Int, val timestamp: Long, val type: Int, var pid: Int = 0, var meshInfo: String? = null, var signature: String? = null) {
    override fun toString(): String {
        val sb=StringBuilder()
        sb.append("guid$guid")
        if (id != null) sb.append("id$id")
        if (meshInfo != null) sb.append("meshInfo$meshInfo")
        sb.append("name$name").append("pid$pid").append("spaceId$spaceId").append("timestamp$timestamp").append("type$type")
        return sb.toString()
    }
}