package com.ihomey.linkuphome.data.vo

import android.arch.persistence.room.*


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "device")
data class SingleDevice(@PrimaryKey var id: Int, @Embedded var device: Device, @ColumnInfo(name = "hash") var hash: Int, @ColumnInfo(name = "groupsSupported") var groupsSupported: Int, @ColumnInfo(name = "modelSupportL") var modelSupportL: Long, @ColumnInfo(name = "modelSupportH") var modelSupportH: Long, @Embedded var state: ControlState?) {

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is SingleDevice) {
            return false
        }
        val singleDevice = obj as SingleDevice?
        if (singleDevice!!.id == this.id ) {
            return true
        }else if(singleDevice.hash == this.hash && singleDevice.device.type == this.device.type){
            return true
        }
        return super.equals(obj)
    }

    @Ignore
    val NUM_BITS_MODEL_SUPPORTED = 64
    @Ignore
    val MODEL_SUPPORT_HIGHEST_BIT_POSITION = NUM_BITS_MODEL_SUPPORTED - 1


    fun getModelsSupported(): ArrayList<Int> {
        val modelsSupported = ArrayList<Int>()

        for (i in 0..127) {
            if (isModelSupported(i))
                modelsSupported.add(i)
        }

        return modelsSupported
    }

    private fun isModelSupported(vararg modelNumber: Int): Boolean {
        if (modelNumber.isEmpty()) {
            return true
        }
        var maskLow: Long = 0
        var maskHigh: Long = 0
        for (n in modelNumber) {
            if (n < NUM_BITS_MODEL_SUPPORTED) {
                maskLow = maskLow or (0x01L shl n)
            } else {
                maskHigh = maskHigh or (0x01L shl n - MODEL_SUPPORT_HIGHEST_BIT_POSITION)
            }
        }
        val resultLow = maskLow and modelSupportL
        val resultHigh = maskHigh and modelSupportH

        return resultLow == maskLow && resultHigh == maskHigh
    }

}

