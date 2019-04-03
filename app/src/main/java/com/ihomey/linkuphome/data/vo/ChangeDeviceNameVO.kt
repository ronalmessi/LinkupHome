package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */

@JsonPropertyOrder("guid","id","name","spaceId","timestamp","type")
data class ChangeDeviceNameVO(val guid: String, val id: Int, val name: String, val spaceId: Int, val timestamp:Long, val type: Int,var signature:String?=null)