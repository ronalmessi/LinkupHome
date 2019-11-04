package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid", "meshInfo", "name", "timestamp")
data class CreateZoneVO(val guid: String, val name: String, val meshInfo: String? = null, val timestamp: Long, var signature: String? = null)