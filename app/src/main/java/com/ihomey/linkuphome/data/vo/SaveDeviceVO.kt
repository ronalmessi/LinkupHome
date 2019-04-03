package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid","name","spaceId","timestamp","type")
data class SaveDeviceVO(val guid: String, val name: String, val spaceId: Int,val timestamp:Long,val type: Int, var signature:String?=null)