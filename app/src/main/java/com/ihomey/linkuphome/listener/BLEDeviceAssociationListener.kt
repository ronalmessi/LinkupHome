package com.ihomey.linkuphome.listener

import android.bluetooth.BluetoothDevice

/**
 * Created by Administrator on 2017/6/21.
 */
interface BLEDeviceAssociationListener {

    fun newDevice(name: String,macAddress:String)


    fun deviceConnecting()


    fun deviceConnected(success:Boolean,address:String)


    fun deviceDisConnected()

}