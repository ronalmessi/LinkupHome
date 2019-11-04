package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid", "id", "meshInfo", "name", "timestamp")
data class UploadMeshInfoVO(val guid: String, val id: Int?, val name: String?, val meshInfo: String, val timestamp: Long, var signature: String? = null)
