package com.ihomey.linkuphome.sigmesh

import com.ihomey.linkuphome.data.entity.Device

/**
 * Created by Administrator on 2017/6/21.
 */

interface MeshDeviceScanListener {
    fun onDeviceFound(device:Device)
//    fun deviceAssociated(deviceId: Int, message: String)
//    fun deviceAssociated(deviceId: Int, uuidHash: Int, macAddress: String)
//    fun associationProgress(progress: Int)
}