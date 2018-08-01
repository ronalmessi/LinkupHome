package com.ihomey.linkuphome.time

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.RadioGroup
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.DeviceInfo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.syncTime
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.viewmodel.MainViewModel_MembersInjector
import com.suke.widget.SwitchButton
import java.util.*

abstract class BaseTimerSettingFragment : BaseFragment(), RadioGroup.OnCheckedChangeListener, View.OnClickListener, SwitchButton.OnCheckedChangeListener{

    private var mControlDevice: ControlDevice? = null
    private var controller: Controller? = null
    private var mDeviceType: Int = -1
    protected var mViewModel: MainViewModel? = null
    protected lateinit var listener: MeshServiceStateListener

    abstract fun updateViewData(controlDevice: ControlDevice)

    abstract fun enableEditTimer(flag: Boolean)

    abstract fun getHour(): Int

    abstract fun getMinute(): Int

    abstract fun isOpenTimer(): Boolean

    abstract fun setTimerOn(flag: Boolean)

    abstract fun isTimerOn(): Boolean

    abstract fun isRepeat(): Boolean


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS&&it.data!=null) {
                mControlDevice = it.data
                initController(it.data.device.type)
                updateViewData(it.data)
            }
        })
    }

    fun initController(deviceType: Int) {
        mDeviceType = deviceType
        controller = ControllerFactory().createController(mDeviceType)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toolbar_back -> activity.onBackPressed()
            R.id.btn_timer_setting_edit -> {
                if (v.tag != null && v.tag as Boolean) {
                    v.tag = null
                    (v as Button).setText(R.string.save)
                    enableEditTimer(true)
                    if (mDeviceType == 4 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                        syncTime(mControlDevice?.id!!)
                    }
                } else {
                    v.tag = true
                    (v as Button).setText(R.string.edit)
                    enableEditTimer(false)
                    saveTime()
                }
            }
        }
    }

    private fun saveTime() {
        if (mControlDevice != null) {
            val lightState = mControlDevice?.state
            if (lightState != null) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, getHour())
                calendar.set(Calendar.MINUTE, getMinute())
                if (mControlDevice?.device?.type == 4) {
                    if (isRepeat()) calendar.set(Calendar.YEAR, 1970)
                    if (isOpenTimer()) {
                        lightState.openTimer = calendar.timeInMillis
                        lightState.openTimerOn = 3
                        if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                            controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), true, isTimerOn(), isRepeat())
                        }
                    } else {
                        lightState.closeTimer = calendar.timeInMillis
                        lightState.closeTimerOn = 3
                        if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                            controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), false, isTimerOn(), isRepeat())
                        }
                    }
                } else {
                    val isExpired = calendar.timeInMillis - System.currentTimeMillis() < 0
                    if (isOpenTimer()) {
                        lightState.openTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
                        lightState.openTimerOn = 3
                    } else {
                        lightState.closeTimerOn = 3
                        lightState.closeTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
                    }
                }
                setTimerOn(true)
            }
            mViewModel?.updateDevice(mControlDevice)
        }
    }

    override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
        setTimerOn(isChecked)
        if (mControlDevice != null) {
            val lightState = mControlDevice?.state
            if (lightState != null) {
                if (isOpenTimer()) {
                    lightState.openTimerOn = if (isChecked) 3 else 2
                    if (isChecked) {
                        if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                            if (mControlDevice?.device?.type != 4) {
                                controller?.setTimer(mControlDevice?.id!!, getPeriodMinute(getHour(), getMinute()), true)
                            } else {
                                controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), true, true, isRepeat())
                            }
                        }
                    } else {
                        if (mControlDevice?.device?.type == 4) {
                            if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                                controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), true, false, isRepeat())
                            }
                        }
                    }
                } else {
                    lightState.closeTimerOn = if (isChecked) 3 else 2
                    if (isChecked) {
                        if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                            if (mControlDevice?.device?.type != 4) {
                                controller?.setTimer(mControlDevice?.id!!, getPeriodMinute(getHour(), getMinute()), false)
                            } else {
                                controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), false, true, isRepeat())
                            }
                        }
                    } else {
                        if (mControlDevice?.device?.type == 4) {
                            if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                                controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), false, false, isRepeat())
                            }
                        }
                    }
                }
            }
            mViewModel?.updateDevice(mControlDevice)
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val radioButtonId = group?.checkedRadioButtonId
        if (mControlDevice != null) {
            val lightState = mControlDevice?.state
            if (lightState != null) {
                if (radioButtonId == R.id.rb_timer_setting_on) {
                    lightState.openTimerOn = lightState.openTimerOn + 2
                    if (lightState.closeTimerOn > 1) {
                        lightState.closeTimerOn = lightState.closeTimerOn - 2
                    }
                } else {
                    lightState.closeTimerOn = lightState.closeTimerOn + 2
                    if (lightState.openTimerOn > 1) {
                        lightState.openTimerOn = lightState.openTimerOn - 2
                    }
                }
            }
            updateViewData(mControlDevice!!)
        }
    }

//    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        Log.d("aa","1111111")
////        setRepeat(!isRepeat())
////        if (mControlDevice != null) {
////            val lightState = mControlDevice?.state
////            if (lightState != null) {
////                val calendar = Calendar.getInstance()
////                calendar.set(Calendar.HOUR_OF_DAY, getHour())
////                calendar.set(Calendar.MINUTE, getMinute())
////                if (isRepeat()) calendar.set(Calendar.YEAR, 1970)
////                if (isOpenTimer()) {
////                    lightState.openTimer = calendar.timeInMillis
////                    if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
////                        controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), true, isTimerOn(), isRepeat())
////                    }
////                } else {
////                    lightState.closeTimer = calendar.timeInMillis
////                    if (mDeviceType != -1 && mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
////                        controller?.setRepeatTimer(mControlDevice?.id!!, getMinute(), getHour(), false, isTimerOn(), isRepeat())
////                    }
////                }
////            }
////            mViewModel?.updateDevice(mControlDevice)
////        }
//    }

//    private fun updateTimerSettingView() {
//        isManualSwitch = false
//        if (mControlDevice != null) {
//            val lightState = mControlDevice?.state
//            if (lightState != null) {
//                if (lightState.closeTimerOn == 0 && lightState.openTimerOn == 0) {
//                    setIsOpenTimer(true, lightState.openTimerOn)
//                } else {
//                    setIsOpenTimer(lightState.openTimerOn > 1, if (lightState.openTimerOn > 1) lightState.openTimerOn else lightState.closeTimerOn)
//                }
//                val time = if (lightState.openTimerOn > 1) lightState.openTimer else lightState.closeTimer
//                val timerOn = if (lightState.openTimerOn > 1) lightState.openTimerOn else lightState.closeTimerOn
//                var isRepeat = false
//                if (time != 0L) {
//                    val calendar = Calendar.getInstance()
//                    calendar.time = Date(time)
//                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
//                    val minute = calendar.get(Calendar.MINUTE)
//                    val year = calendar.get(Calendar.YEAR)
//                    isRepeat = year == 1970
//                    setTimer(hour, minute)
//                } else {
//                    setTimer(0, 0)
//                }
//                if (mControlDevice?.device?.type != 4) {
//                    if (System.currentTimeMillis() > time) {
//                        setTimerOn(false)
//                    } else {
//                        setTimerOn(timerOn == 3)
//                    }
//                } else {
//                    setTimerOn(timerOn == 3)
//                    setRepeat(isRepeat)
//                }
//            } else {
//                setTimer(0, 0)
//            }
//        }
//    }

    protected fun getMinuteList(): ArrayList<String> {
        val list = ArrayList<String>()
        for (i in 0..59) {
            if (i < 10) {
                list.add("0$i")
            } else {
                list.add("" + i)
            }
        }
        return list
    }

    protected fun getHourList(): ArrayList<String> {
        val list = ArrayList<String>()
        for (i in 0..23) {
            if (i < 10) {
                list.add("0$i")
            } else {
                list.add("" + i)
            }
        }
        return list
    }

    private fun getPeriodMinute(selectHour: Int, selectMinute: Int): Int {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        return if (selectHour > currentHour || selectHour == currentHour && selectMinute >= currentMinute) {
            (selectHour - currentHour) * 60 + selectMinute - currentMinute
        } else {
            (selectHour - currentHour + 24) * 60 + selectMinute - currentMinute
        }
    }
}