package com.ihomey.linkuphome.controller


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


    open fun setSleepMode(state: Int) {}

    open fun enableGestureControl(isEnable: Boolean) {}

    open fun syncTime() {}

}
