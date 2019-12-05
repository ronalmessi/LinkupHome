package com.ihomey.linkuphome.protocol.sigmesh

/**
 * Created by Administrator on 2017/6/21.
 */
interface MeshDeviceAssociateListener {
    fun deviceAssociateFailed(messageRes: Int)
    fun deviceAssociated(deviceId: Int, uuidHash: Int, macAddress: String)
    fun associationProgress(progress: Int)
}