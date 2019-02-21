package com.ihomey.linkuphome.time

import android.annotation.SuppressLint
import androidx.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.databinding.FragmentTimerSettingRepeatBinding
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class RepeatTimerSettingFragment : BaseTimerSettingFragment() {
    override fun updateViewData(singleDevice: SingleDevice) {

    }

    lateinit var mViewDataBinding: FragmentTimerSettingRepeatBinding

    fun newInstance(): RepeatTimerSettingFragment {
        return RepeatTimerSettingFragment()
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
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer_setting_repeat, container, false)
        mViewDataBinding.wheelTimerHour.data = getHourList()
        mViewDataBinding.wheelTimerMinute.data = getMinuteList()
        mViewDataBinding.btnTimerSettingEdit.tag = true
        return mViewDataBinding.root
    }


//    override fun updateViewData(controlDevice: ControlDevice) {
//        val lightState = controlDevice.state
//        if (lightState.openTimerOn > 1 || lightState.closeTimerOn > 1) {
//            var alarmTime: Long? = null
//            if (lightState.openTimerOn > 1) {
//                mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bbF48479"))
//                mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bbF48479"))
//                mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_on)
//                mViewDataBinding.rbTimerSettingOn.isChecked = true
//                alarmTime = lightState.openTimer
//
//                mViewDataBinding.switchButtonTimer.isChecked = lightState.openTimerOn == 3
//                mViewDataBinding.frameLayoutTimerSetting.isActivated =lightState.openTimerOn == 3
//
//            } else {
//                mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bb949494"))
//                mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bb949494"))
//                mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_off)
//                mViewDataBinding.rbTimerSettingOff.isChecked = true
//                alarmTime = lightState.closeTimer
//
//                mViewDataBinding.switchButtonTimer.isChecked =lightState.closeTimerOn == 3
//                mViewDataBinding.frameLayoutTimerSetting.isActivated = lightState.closeTimerOn == 3
//            }
//            mViewDataBinding.btnTimerSettingEdit.visibility = View.VISIBLE
//            mViewDataBinding.switchButtonTimer.visibility = View.VISIBLE
//            if (alarmTime != 0L) {
//                val calendar = Calendar.getInstance()
//                calendar.time = Date(alarmTime)
//                mViewDataBinding.cbTimerSettingRepeat.isChecked = calendar.get(Calendar.YEAR) == 1970
//                mViewDataBinding.wheelTimerHour.selectedItemPosition = calendar.get(Calendar.HOUR_OF_DAY)
//                mViewDataBinding.wheelTimerMinute.selectedItemPosition = calendar.get(Calendar.MINUTE)
//            } else {
//                mViewDataBinding.wheelTimerHour.selectedItemPosition = 0
//                mViewDataBinding.wheelTimerMinute.selectedItemPosition = 0
//            }
//        } else {
//            mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bbF48479"))
//            mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bbF48479"))
//            mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_on)
//            mViewDataBinding.btnTimerSettingEdit.visibility = View.INVISIBLE
//            mViewDataBinding.switchButtonTimer.visibility = View.INVISIBLE
//        }
//        mViewDataBinding.rgTimerSetting.setOnCheckedChangeListener(this)
//        mViewDataBinding.switchButtonTimer.setOnCheckedChangeListener(this)
//        mViewDataBinding.btnTimerSettingEdit.setOnClickListener(this)
//        mViewDataBinding.toolbarBack.setOnClickListener(this)
//    }

    override fun enableEditTimer(flag: Boolean) {
        mViewDataBinding.wheelTimerHour.setEditable(flag)
        mViewDataBinding.wheelTimerMinute.setEditable(flag)
        mViewDataBinding.rbTimerSettingOn.visibility = if (flag) View.GONE else View.VISIBLE
        mViewDataBinding.rbTimerSettingOff.visibility = if (flag) View.GONE else View.VISIBLE
        mViewDataBinding.rbTimerSettingOn.visibility = if (flag) View.GONE else View.VISIBLE
        mViewDataBinding.cbTimerSettingRepeat.visibility = if (flag) View.VISIBLE else View.GONE
        mViewDataBinding.switchButtonTimer.visibility = if(flag) View.INVISIBLE else View.VISIBLE
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
        return mViewDataBinding.cbTimerSettingRepeat.isChecked
    }

}

