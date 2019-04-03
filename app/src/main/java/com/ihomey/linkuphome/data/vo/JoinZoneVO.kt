package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("code","guid","timestamp")
data class JoinZoneVO(val code: String,val guid: String,val timestamp:Long, var signature:String?=null)