package com.ihomey.linkuphome.listeners

import java.util.*

/**
 * Created by Administrator on 2017/6/21.
 */
interface DeviceAssociateListener {
    fun newAppearance(uuidHash: Int, appearance: ByteArray, shortName: String)
    fun deviceAssociated(deviceId: Int,message: String)
    fun deviceAssociated(deviceId: Int, uuidHash: Int, bitmap: Long)
    fun associationProgress(progress: Int)
}