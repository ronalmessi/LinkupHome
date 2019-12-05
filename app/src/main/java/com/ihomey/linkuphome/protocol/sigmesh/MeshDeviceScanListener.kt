package com.ihomey.linkuphome.protocol.sigmesh

import com.ihomey.linkuphome.data.entity.Device

/**
 * Created by Administrator on 2017/6/21.
 */

interface MeshDeviceScanListener {
    fun onDeviceFound(device:Device)
}