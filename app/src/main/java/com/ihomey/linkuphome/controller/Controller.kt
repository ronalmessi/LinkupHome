package com.ihomey.linkuphome.controller


/**
 * Created by Administrator on 2017/6/16.
 */
abstract class Controller {


    open fun setLightPowerState(deviceAddress: String?, powerState: Int) {}

    open fun setLightBright(deviceAddress: String?, brightValue: Int) {}

    open fun setSleepMode(deviceAddress: String?, state: Int) {}

    open fun enableGestureControl(deviceAddress: String?, isEnable: Boolean) {}

    open fun syncTime(deviceAddress: String?) {}

    open fun setLightColor(deviceAddress: String?, colorValue: String) {}

    open fun setLightSpeed(deviceAddress: String?, speedValue: Int) {}

    open fun setLightColorTemperature(deviceAddress: String?, colorTemperature: Int) {}

    open fun setLightingMode(deviceAddress: String?) {}

    open fun setLightScene(deviceAddress: String?, sceneValue: Int) {}

    open fun setRepeatTimer(deviceAddress: String?, minuteValue: Int, hourValue: Int, isOpenTimer: Boolean, isOn: Boolean, dayOfWeek: Int) {}

}
