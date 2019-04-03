package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */

@JsonPropertyOrder("guid","id","name","timestamp","value")
data class ChangeDeviceStateVO(val guid: String, val id: Int, val name: String, val value: String, val timestamp:Long, var signature:String?=null)