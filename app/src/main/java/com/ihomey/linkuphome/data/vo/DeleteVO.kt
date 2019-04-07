package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid","id","timestamp")
data class DeleteVO(val guid: String, val id: Int, val timestamp:Long, var signature:String?=null)