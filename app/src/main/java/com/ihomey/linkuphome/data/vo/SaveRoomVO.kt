package com.ihomey.linkuphome.data.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.ihomey.linkuphome.data.entity.*


/**
 * Created by dongcaizheng on 2018/4/11.
 */
@JsonPropertyOrder("guid","name","spaceId","timestamp","type")
data class SaveRoomVO(val guid: String, val name: String, val spaceId: Int,val timestamp:Long, val type: Int, var signature:String?=null)