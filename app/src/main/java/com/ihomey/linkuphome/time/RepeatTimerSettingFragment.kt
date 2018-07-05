package com.ihomey.linkuphome.time

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentTimerSettingRepeatBinding


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class RepeatTimerSettingFragment : BaseTimerSettingFragment(){



    lateinit var mViewDataBinding: FragmentTimerSettingRepeatBinding

    fun newInstance(deviceId: Int, deviceType: Int): RepeatTimerSettingFragment {
        val fragment = RepeatTimerSettingFragment()
        val bundle = Bundle()
        bundle.putInt("deviceId", deviceId)
        bundle.putInt("deviceType", deviceType)
        fragment.arguments = bundle
        return fragment
    }

    override fun getHour(): Int {
        return mViewDataBinding.tvTimerSettingHourEdit.selectedItem
    }

    override fun getMinute(): Int {
        return mViewDataBinding.tvTimerSettingMinuteEdit.selectedItem
    }

    override fun isOpenTimer(): Boolean {
        return mViewDataBinding.rgTimerSetting.checkedRadioButtonId == R.id.rb_timer_setting_on
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer_setting_repeat, container, false)
        initController(arguments.getInt("deviceType", -1))

        mViewDataBinding.tvTimerSettingHourEdit.setLabel("h")
        mViewDataBinding.tvTimerSettingHourEdit.setTextSize(48f)
        mViewDataBinding.tvTimerSettingHourEdit.setDataList(getHourList())

        mViewDataBinding.tvTimerSettingMinuteEdit.setLabel("m");
        mViewDataBinding.tvTimerSettingMinuteEdit.setTextSize(48f)
        mViewDataBinding.tvTimerSettingMinuteEdit.setDataList(getMinuteList())


        mViewDataBinding.btnTimerSettingEdit.tag = true

        mViewDataBinding.btnTimerSettingEdit.setOnClickListener(this)
        mViewDataBinding.rgTimerSetting.setOnCheckedChangeListener(this)
        mViewDataBinding.switchButtonTimer.setOnCheckedChangeListener(this)
        mViewDataBinding.switchButtonTimer.setOnTouchListener(this)
        mViewDataBinding.switchButtonTimer.setOnClickListener { Log.d("lamp_icon_bed_unadded","hhhhh")}
        mViewDataBinding.cbTimerSettingRepeat.setOnCheckedChangeListener(this)

        mViewDataBinding.toolbarBack.setOnClickListener(this)
        return mViewDataBinding.root
    }


    override fun enableEditTimer(flag: Boolean) {
        if (flag) {
            mViewDataBinding.tvTimerSettingMinuteEdit.setCanEdit(true)
            mViewDataBinding.tvTimerSettingHourEdit.setCanEdit(true)
        } else {
            mViewDataBinding.tvTimerSettingMinuteEdit.setCanEdit(false)
            mViewDataBinding.tvTimerSettingHourEdit.setCanEdit(false)
        }
    }

    override fun setIsOpenTimer(flag: Boolean) {
        if (flag) {
            mViewDataBinding.tvTimerSettingHourEdit.setCircleColor(1)
            mViewDataBinding.tvTimerSettingMinuteEdit.setCircleColor(1)
            mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.mipmap.timer_setting_repeat_on_bg)
        } else {
            mViewDataBinding.tvTimerSettingHourEdit.setCircleColor(0)
            mViewDataBinding.tvTimerSettingMinuteEdit.setCircleColor(0)
            mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.mipmap.timer_setting_repeat_off_bg)
        }
    }


    override fun setTimer(hour: Int, minute: Int) {
        mViewDataBinding.tvTimerSettingHourEdit.setInitPosition(hour)
        mViewDataBinding.tvTimerSettingMinuteEdit.setInitPosition(minute)
    }

    override fun setTimerOn(flag: Boolean) {
        mViewDataBinding.switchButtonTimer.isChecked = flag
    }

    override fun isTimerOn(): Boolean {
        return mViewDataBinding.switchButtonTimer.isChecked
    }

    override fun isRepeat(): Boolean {
        return mViewDataBinding.cbTimerSettingRepeat.isActivated
    }

    override fun setRepeat(flag: Boolean) {
        mViewDataBinding.cbTimerSettingRepeat.isActivated=flag
    }
}

