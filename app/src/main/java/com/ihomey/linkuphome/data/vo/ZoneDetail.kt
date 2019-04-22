package com.ihomey.linkuphome.data.vo

import com.ihomey.linkuphome.data.entity.*
import com.ihomey.linkuphome.data.entity.Device

/**
 * Created by dongcaizheng on 2018/4/11.
 */
data class ZoneDetail(val id: Int, var name: String, val netWorkKey: String, val nextDeviceIndex: Int, val nextGroupIndex: Int, val active:Int,val type:Int, val groups:List<Room>?, val devices:List<Device>?){

}