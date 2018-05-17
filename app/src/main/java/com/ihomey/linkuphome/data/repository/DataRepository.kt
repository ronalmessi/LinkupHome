package com.ihomey.linkuphome.data.repository

import android.util.SparseArray
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.data.db.GroupDeviceDao
import com.ihomey.linkuphome.data.db.LampCategoryDao
import com.ihomey.linkuphome.data.db.ModelDao
import com.ihomey.linkuphome.data.db.SingleDeviceDao
import com.ihomey.linkuphome.data.vo.*
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by dongcaizheng on 2018/4/9.
 */
@Singleton
class DataRepository @Inject constructor(private val lampGroupDao: GroupDeviceDao, private val lampCategoryDao: LampCategoryDao, private val singleDeviceDao: SingleDeviceDao, private val modelDao: ModelDao, private var appExecutors: AppExecutors) {

    fun updateData(jsonObj: JSONObject) {
        appExecutors.diskIO().execute {
            val mDeviceType = jsonObj.getInt(DEVICE_TYPE) - 1

            val currentId = jsonObj.getInt(CURRENT_ID)
            var lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$mDeviceType", -1)
            lastUsedDeviceId = currentId

            var isShare by PreferenceHelper("share_$mDeviceType", false)
            isShare = true

            //Setting
            val globalSetting=lampCategoryDao.getSetting(-1)
            val nextDeviceIndex = jsonObj.getInt(NEXT_DEVICE_INDEX_KEY)
            val nextGroupIndex = jsonObj.getInt(NEXT_GROUP_INDEX_KEY)
            if(globalSetting.nextDeviceIndex<nextDeviceIndex||globalSetting.nextGroupIndex<nextGroupIndex){
                globalSetting.nextDeviceIndex=nextDeviceIndex
                globalSetting.nextGroupIndex=nextGroupIndex
                lampCategoryDao.updateCategory(globalSetting)
            }

            val networkKey = jsonObj.getString(NETWORK_KEY)

            lampCategoryDao.updateNetWorkkey(networkKey, mDeviceType)

            val devices = jsonObj.getJSONArray(DEVICES_KEY)
            val groups = jsonObj.getJSONArray(GROUPS_KEY)
            val lightStateJsonArray = jsonObj.getJSONArray("exts")

            val stateArray = SparseArray<ControlState>()
            // looping through lightStates
            for (i in 0 until lightStateJsonArray.length()) {
                var lightState: ControlState? = null
                val it = lightStateJsonArray.getJSONObject(i).keys()
                var deviceId: String? = null
                while (it.hasNext()) {
                    deviceId = it.next()
                    val jsonObject = JSONObject(lightStateJsonArray.getJSONObject(i).getString(deviceId))
                    val isOn = if (jsonObject.getBoolean("isOn")) 1 else 0
                    val colorTemperature = jsonObject.getInt("colorTemperature")
                    val isOnOpenTimer = if (jsonObject.getBoolean("isOnOpenTimer")) 1 else 0
                    val isOnCloseTimer = if (jsonObject.getBoolean("isOnCloseTimer")) 1 else 0
                    val radian = java.lang.Float.valueOf(jsonObject.get("colorPosition").toString())!!
                    val colorPosition = if (radian < 0 && radian >= -Math.PI) -radian else Math.PI.toFloat() * 2 - radian
                    val sceneMode = Integer.parseInt(Integer.toHexString(jsonObject.getInt("sceneMode"))[1] + "")
                    val changeMode = if (jsonObject.getBoolean("isSelectedChangeMode")) jsonObject.getInt("changeMode") else -1
                    val isLight = if (jsonObject.getBoolean("isLight")) 1 else 0
                    val brightness = jsonObject.getInt("brightness") - 15
                    val openTimer = jsonObject.getLong("openTimer")
                    val closeTimer = jsonObject.getLong("closeTimer")
                    lightState = ControlState(isOn, isLight, changeMode, colorPosition, colorTemperature, brightness, sceneMode, openTimer, closeTimer, isOnOpenTimer, isOnCloseTimer)
                }
                if (deviceId != null && lightState != null) {
                    stateArray.put(deviceId.toInt(), lightState)
                }
            }

            val groupDevices = ArrayList<GroupDevice>()
            // looping through groups
            for (i in 0 until groups.length()) {
                val groupId = groups.getJSONObject(i).getInt(GROUP_ID_KEY)
                val groupName = groups.getJSONObject(i).getString(GROUP_NAME_KEY)
                val group = GroupDevice(groupId, Device(groupName, mDeviceType), stateArray.get(groupId))
                groupDevices.add(group)
            }

            val singleDevices = ArrayList<SingleDevice>()
            val modelList = ArrayList<Model>()
            // looping through devices
            for (devicesIndex in 0 until devices.length()) {
                val deviceId = devices.getJSONObject(devicesIndex).getInt(DEVICE_ID_KEY)
                val deviceName = devices.getJSONObject(devicesIndex).getString(DEVICE_NAME_KEY)
                val uuidHash = devices.getJSONObject(devicesIndex).getInt(DEVICE_HASH_KEY)
                val device = SingleDevice(deviceId, Device(deviceName, mDeviceType), uuidHash, 0, 0, 0, stateArray.get(deviceId))
                val models = devices.getJSONObject(devicesIndex).getJSONArray(DEVICE_MODELS_KEY)
                for (modelsIndex in 0 until models.length()) {
                    val groupInstances = models.getJSONObject(modelsIndex).getJSONArray(MODEL_GROUP_INSTANCES_KEY)
                    for (i in 0 until groupInstances.length()) {
                        val groupX = groupInstances.getJSONObject(i).getInt(MODEL_GROUP_X_KEY)
                        if (groupX != 0) {
                            val model = Model(null, deviceId, groupX, 0, mDeviceType)
                            if (!modelList.contains(model)) {
                                modelList.add(model)
                            }
                        }
                    }
                }
                singleDevices.add(device)
            }

            // reset database
            singleDeviceDao.deleteByType(mDeviceType)
            lampGroupDao.deleteByType(mDeviceType)

            singleDeviceDao.insertDevices(toArray<SingleDevice>(singleDevices))
            lampGroupDao.insertGroups(toArray<GroupDevice>(groupDevices))
            modelDao.insertModels(toArray<Model>(modelList))

        }
    }

    inline fun <reified T> toArray(list: List<*>): Array<T> {
        return (list as List<T>).toTypedArray()
    }
}



