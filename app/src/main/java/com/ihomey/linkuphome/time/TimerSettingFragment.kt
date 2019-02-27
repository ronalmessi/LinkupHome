package com.ihomey.linkuphome.time

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.databinding.FragmentTimerSettingBinding
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class TimerSettingFragment : BaseTimerSettingFragment() {

    lateinit var mViewDataBinding: FragmentTimerSettingBinding
    fun newInstance(): TimerSettingFragment {
        return TimerSettingFragment()
    }

    override fun getHour(): Int {
        return mViewDataBinding.wheelTimerHour.currentItemPosition
    }

    override fun getMinute(): Int {
        return mViewDataBinding.wheelTimerMinute.currentItemPosition
    }

    override fun isOpenTimer(): Boolean {
        return mViewDataBinding.rgTimerSetting.checkedRadioButtonId == R.id.rb_timer_setting_on
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer_setting, container, false)
        mViewDataBinding.wheelTimerHour.data = getHourList()
        mViewDataBinding.wheelTimerMinute.data = getMinuteList()
        mViewDataBinding.btnTimerSettingEdit.tag = true
        return mViewDataBinding.root
    }


    override fun updateViewData(singleDevice: SingleDevice) {
        val lightState = singleDevice.state
        if (lightState != null) {
            if (lightState.openTimerOn > 1 || lightState.closeTimerOn > 1) {
                var alarmTime: Long? = null
                if (lightState.openTimerOn > 1) {
                    mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bbF48479"))
                    mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bbF48479"))
                    mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_on)
                    mViewDataBinding.rbTimerSettingOn.isChecked = true
                    alarmTime = lightState.openTimer

                    mViewDataBinding.switchButtonTimer.isChecked = System.currentTimeMillis() <= alarmTime && lightState.openTimerOn == 3
                    mViewDataBinding.frameLayoutTimerSetting.isActivated = System.currentTimeMillis() <= alarmTime && lightState.openTimerOn == 3

                } else {
                    mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bb949494"))
                    mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bb949494"))
                    mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_off)
                    mViewDataBinding.rbTimerSettingOff.isChecked = true
                    alarmTime = lightState.closeTimer

                    mViewDataBinding.switchButtonTimer.isChecked = System.currentTimeMillis() <= alarmTime && lightState.closeTimerOn == 3
                    mViewDataBinding.frameLayoutTimerSetting.isActivated = System.currentTimeMillis() <= alarmTime && lightState.closeTimerOn == 3
                }
                mViewDataBinding.btnTimerSettingEdit.visibility = View.VISIBLE
                mViewDataBinding.switchButtonTimer.visibility = View.VISIBLE
                if (alarmTime != 0L) {
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(alarmTime)
                    mViewDataBinding.wheelTimerHour.selectedItemPosition = calendar.get(Calendar.HOUR_OF_DAY)
                    mViewDataBinding.wheelTimerMinute.selectedItemPosition = calendar.get(Calendar.MINUTE)
                } else {
                    mViewDataBinding.wheelTimerHour.selectedItemPosition = 0
                    mViewDataBinding.wheelTimerMinute.selectedItemPosition = 0
                }
            } else {
                mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bbF48479"))
                mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bbF48479"))
                mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_on)
                mViewDataBinding.btnTimerSettingEdit.visibility = View.INVISIBLE
                mViewDataBinding.switchButtonTimer.visibility = View.INVISIBLE
            }
        }

        mViewDataBinding.rgTimerSetting.setOnCheckedChangeListener(this)
        mViewDataBinding.switchButtonTimer.setOnCheckedChangeListener(this)
        mViewDataBinding.btnTimerSettingEdit.setOnClickListener(this)
        mViewDataBinding.toolbarBack.setOnClickListener(this)
    }


    override fun enableEditTimer(flag: Boolean) {
        mViewDataBinding.wheelTimerHour.setEditable(flag)
        mViewDataBinding.wheelTimerMinute.setEditable(flag)
        mViewDataBinding.switchButtonTimer.visibility = if (flag) View.INVISIBLE else View.VISIBLE
        mViewDataBinding.frameLayoutTimerSetting.isActivated = !flag
    }

    override fun setTimerOn(flag: Boolean) {
        mViewDataBinding.switchButtonTimer.isChecked = flag
        mViewDataBinding.frameLayoutTimerSetting.isActivated = flag
    }

    override fun isTimerOn(): Boolean {
        return mViewDataBinding.switchButtonTimer.isChecked
    }

    override fun isRepeat(): Boolean {
        return false
    }


}