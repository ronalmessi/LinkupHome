package com.ihomey.linkuphome.data.entity

import androidx.room.*
import com.ihomey.linkuphome.data.vo.ControlState
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Entity(tableName = "device",primaryKeys =["id","zoneId"],foreignKeys =[(ForeignKey(entity = Zone::class, parentColumns = arrayOf("id"), childColumns = arrayOf("zoneId"), onDelete = ForeignKey.CASCADE))])
data class SingleDevice(var id: Int,val zoneId: Int,var name: String, val type: Int,var hash: Int, var groupsSupported: Int, var modelSupportL: Long,var modelSupportH: Long, @Embedded var state: ControlState) {

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is SingleDevice) {
            return false
        }
        val singleDevice = obj as SingleDevice?
       if(singleDevice?.hash == this.hash){
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

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + zoneId
        result = 31 * result + name.hashCode()
        result = 31 * result + type
        result = 31 * result + hash
        result = 31 * result + groupsSupported
        result = 31 * result + modelSupportL.hashCode()
        result = 31 * result + modelSupportH.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + NUM_BITS_MODEL_SUPPORTED
        result = 31 * result + MODEL_SUPPORT_HIGHEST_BIT_POSITION
        return result
    }


    constructor(id: Int,zoneId:Int,name: String,type: Int, hash: Int, groupsSupported: Int,  modelSupportL: Long, modelSupportH: Long):this(id,zoneId,name,type, hash, groupsSupported, modelSupportL, modelSupportH, ControlState())

}

