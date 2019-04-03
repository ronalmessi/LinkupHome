package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid","name","timestamp")
data class CreateZoneVO(val guid: String, val name: String, val timestamp:Long, var signature:String?=null)