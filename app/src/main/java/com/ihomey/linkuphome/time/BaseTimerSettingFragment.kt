package com.ihomey.linkuphome.time

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.syncTime
import com.suke.widget.SwitchButton
import java.util.*

abstract class BaseTimerSettingFragment : BaseFragment(), RadioGroup.OnCheckedChangeListener, View.OnClickListener, SwitchButton.OnCheckedChangeListener {

    private lateinit var mControlDevice: SingleDevice
    private var controller: Controller? = null
    private var mDeviceType: Int = -1
    protected var mViewModel: HomeActivityViewModel? = null
    protected lateinit var listener: MeshServiceStateListener

    abstract fun updateViewData(singleDevice: SingleDevice)

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
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<SingleDevice> {
            mControlDevice = it
            initController(mControlDevice.type)
            updateViewData(mControlDevice)
        })
    }

    fun initController(deviceType: Int) {
        mDeviceType = deviceType
        controller = ControllerFactory().createController(mDeviceType)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toolbar_back -> activity?.onBackPressed()
            R.id.btn_timer_setting_edit -> {
                if (v.tag != null && v.tag as Boolean) {
                    v.tag = null
                    (v as Button).setText(R.string.save)
                    enableEditTimer(true)
                    if (mDeviceType == 5 && listener.isMeshServiceConnected()) {
                        syncTime(mControlDevice.id)
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
        val lightState = mControlDevice.state

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, getHour())
        calendar.set(Calendar.MINUTE, getMinute())
        if (mControlDevice.type == 5) {
            if (isRepeat()) calendar.set(Calendar.YEAR, 1970)
            if (isOpenTimer()) {
                lightState.openTimer = calendar.timeInMillis
                lightState.openTimerOn = 3
                if (mDeviceType != -1 && listener.isMeshServiceConnected()) {
                    controller?.setRepeatTimer(mControlDevice.id, getMinute(), getHour(), true, isTimerOn(), isRepeat())
                }
            } else {
                lightState.closeTimer = calendar.timeInMillis
                lightState.closeTimerOn = 3
                if (mDeviceType != -1 && listener.isMeshServiceConnected()) {
                    controller?.setRepeatTimer(mControlDevice.id, getMinute(), getHour(), false, isTimerOn(), isRepeat())
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
        mViewModel?.updateDevice(mControlDevice)

    }

    override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
        setTimerOn(isChecked)
        val lightState = mControlDevice.state

        if (isOpenTimer()) {
            lightState.openTimerOn = if (isChecked) 3 else 2
            if (isChecked) {
                if (mDeviceType != -1 && listener.isMeshServiceConnected()) {
                    if (mControlDevice.type != 5) {
                        controller?.setTimer(mControlDevice.id, getPeriodMinute(getHour(), getMinute()), true)
                    } else {
                        controller?.setRepeatTimer(mControlDevice.id, getMinute(), getHour(), true, true, isRepeat())
                    }
                }
            } else {
                if (mControlDevice.type == 5) {
                    if (mDeviceType != -1 && listener.isMeshServiceConnected()) {
                        controller?.setRepeatTimer(mControlDevice.id, getMinute(), getHour(), true, false, isRepeat())
                    }
                }
            }
        } else {
            lightState.closeTimerOn = if (isChecked) 3 else 2
            if (isChecked) {
                if (mDeviceType != -1 && listener.isMeshServiceConnected()) {
                    if (mControlDevice.type != 5) {
                        controller?.setTimer(mControlDevice.id, getPeriodMinute(getHour(), getMinute()), false)
                    } else {
                        controller?.setRepeatTimer(mControlDevice.id, getMinute(), getHour(), false, true, isRepeat())
                    }
                }
            } else {
                if (mControlDevice.type == 5) {
                    if (mDeviceType != -1 && listener.isMeshServiceConnected()) {
                        controller?.setRepeatTimer(mControlDevice.id, getMinute(), getHour(), false, false, isRepeat())
                    }
                }
            }

            mViewModel?.updateDevice(mControlDevice)
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val radioButtonId = group?.checkedRadioButtonId
        val lightState = mControlDevice.state
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

        updateViewData(mControlDevice)
    }

//    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        Log.d("bg_splash","1111111")
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