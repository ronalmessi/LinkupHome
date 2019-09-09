package com.ihomey.linkuphome.listener

/**
 * Created by Administrator on 2017/6/21.
 */
interface DeviceAssociateListener {
    fun onDeviceFound(uuidHash: String, macAddress: String, name: String)
    fun deviceAssociated(deviceId: Int,message: String)
    fun deviceAssociated(deviceId: Int, uuidHash: Int, bitmap: Long)
    fun associationProgress(progress: Int)
}