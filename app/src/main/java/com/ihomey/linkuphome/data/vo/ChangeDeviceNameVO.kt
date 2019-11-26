package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */

@JsonPropertyOrder("guid", "id", "name", "pid", "spaceId", "timestamp", "type")
data class ChangeDeviceNameVO(val guid: String, val id: String, val name: String, val spaceId: Int, val timestamp: Long, val type: Int, var signature: String? = null)