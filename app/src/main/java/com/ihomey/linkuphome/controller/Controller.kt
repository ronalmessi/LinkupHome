package com.ihomey.linkuphome.controller




/**
 * Created by Administrator on 2017/6/16.
 */
abstract class Controller {

    open fun setLightColor(deviceId: Int, colorValue: String){}

    open fun setLightColor(mac: String, colorValue: String){}

    open fun setLightBright(deviceId: Int, brightValue: Int){}

    open fun setLightBright(mac: String, brightValue: Int){}

    open fun setLightSpeed(deviceId: Int, speedValue: Int){}

    open fun setLightSpeed(mac: String, speedValue: Int){}

    open fun setLightColorTemperature(deviceId: Int, colorTemperature: Int){}

    open fun setLightColorTemperature(mac: String, colorTemperature: Int){}

    open fun setLightingMode(deviceId: Int){}

    open fun setLightPowerState(deviceId: Int, powerState: Int){}

    open fun setLightPowerState(mac: String, powerState: Int){}

    open fun setGestureState(mac: String, isOn: Boolean){}

    open fun setLightScene(deviceId: Int, sceneValue: Int){}

    open fun setLightScene(mac: String, sceneValue: Int){}

    open fun setTimer(deviceId: Int, minuteValue: Int, isOn: Boolean){}

    open fun setRepeatTimer(deviceId: Int, minuteValue: Int, hourValue: Int, isOpenTimer: Boolean,isOn: Boolean,isRepeat: Boolean){}

    open fun setRepeatTimer(mac: String, minuteValue: Int, hourValue: Int, isOpenTimer: Boolean,isOn: Boolean,dayOfWeek: Int){}

}