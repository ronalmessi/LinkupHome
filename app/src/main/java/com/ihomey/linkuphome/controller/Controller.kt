package com.ihomey.linkuphome.controller

import com.ihomey.linkuphome.data.entity.Alarm


/**
 * Created by Administrator on 2017/6/16.
 */
abstract class Controller {

    open fun setLightColor(deviceId: Int, colorValue: String){}

    open fun setLightBright(deviceId: Int, brightValue: Int){}

    open fun setLightSpeed(deviceId: Int, speedValue: Int){}

    open fun setLightColorTemperature(deviceId: Int, colorTemperature: Int){}

    open fun setLightingMode(deviceId: Int){}

    open fun setLightPowerState(deviceId: Int, powerState: Int){}

    open fun setLightScene(deviceId: Int, sceneValue: Int){}

    open fun setTimer(deviceId: Int, minuteValue: Int, isOn: Boolean){}

    open fun setRepeatTimer(deviceId: Int, minuteValue: Int, hourValue: Int, isOpenTimer: Boolean,isOn: Boolean,isRepeat: Boolean){}

    open fun setRepeatTimer(minuteValue: Int, hourValue: Int, isOpenTimer: Boolean,isOn: Boolean,dayOfWeek: Int){}




    open fun setLightPowerState(deviceAddress:String?, powerState: Int){}

    open fun setLightBright(deviceAddress:String?, brightValue: Int){}

    open fun setSleepMode(deviceAddress:String?,state: Int) {}

    open fun enableGestureControl(deviceAddress:String?,isEnable: Boolean) {}

    open fun syncTime(deviceAddress:String?) {}

    open fun setLightColor(deviceAddress:String?, colorValue: String){}

    open fun setLightSpeed(deviceAddress:String?, speedValue: Int){}

    open fun setLightColorTemperature(deviceAddress:String?, colorTemperature: Int){}

    open fun setLightingMode(deviceAddress:String?){}

    open fun setLightScene(deviceAddress:String?, sceneValue: Int){}

}
