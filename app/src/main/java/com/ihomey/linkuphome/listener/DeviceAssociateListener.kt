package com.ihomey.linkuphome.listener

/**
 * Created by Administrator on 2017/6/21.
 */
interface DeviceAssociateListener {
    fun onDeviceFound(uuidHash: String, macAddress: String, name: String)
    fun deviceAssociated(deviceId: Int,message: String)
    fun deviceAssociated(deviceId: Int, uuidHash: Int, macAddress: String)
    fun associationProgress(progress: Int)
}