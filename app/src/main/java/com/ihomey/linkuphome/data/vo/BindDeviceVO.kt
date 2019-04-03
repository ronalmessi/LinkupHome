package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */

@JsonPropertyOrder("act","deviceInstructId","groupInstructId","guid","spaceId","timestamp")
data class BindDeviceVO(val guid: String, val spaceId: Int, val groupInstructId: Int, val deviceInstructId: Int,val act: String,val timestamp:Long,var signature:String?=null)