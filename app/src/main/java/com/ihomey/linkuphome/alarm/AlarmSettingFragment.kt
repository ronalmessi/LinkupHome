package com.ihomey.linkuphome.alarm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.controller.BedController
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentAlarmSettingBinding
import com.ihomey.linkuphome.dayOfWeek
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.ringTypeNames
import com.ihomey.linkuphome.viewmodel.MainViewModel

import com.jackandphantom.blurimage.BlurImage
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2017/12/25.
 */
class AlarmSettingFragment : BaseFragment(), View.OnClickListener, SwitchButton.OnCheckedChangeListener {

    private lateinit var mViewDataBinding: FragmentAlarmSettingBinding

    private var mainViewModel: MainViewModel? = null
    private var alarmViewModel: AlarmViewModel? = null
    private val controller: BedController = BedController()
    private var deviceId = -1
    private var deviceMac: String? = null

    fun newInstance(): AlarmSettingFragment {
        return AlarmSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_setting, container, false)
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bitmap = BlurImage.with(context).load(R.drawable.bg_alarm_setting).intensity(20f).imageBlur
        view?.background = BitmapDrawable(resources, bitmap)
        mViewDataBinding.rlAlarmSettingRing.setOnClickListener(this)
        mViewDataBinding.rlAlarmSettingRepeat.setOnClickListener(this)
        mViewDataBinding.toolbarBack.setOnClickListener { activity.onBackPressed() }
        mViewDataBinding.toolbarSave.setOnClickListener { saveAlarm() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mainViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                deviceId = it.data.id
                deviceMac = it.data.device.macAddress
                Log.d("aa", "deviceId--" + it.data.id)
            }
        })
        alarmViewModel = ViewModelProviders.of(parentFragment).get(AlarmViewModel::class.java)
        alarmViewModel?.getAlarm()?.observe(this, Observer<Alarm> {
            showAlarmInfo(it)
        })
    }

    private fun showAlarmInfo(alarm: Alarm?) {
        if (alarm?.ringType!! > 0) mViewDataBinding.tvAlarmSettingRingValue.text = ringTypeNames[alarm.ringType - 1]
        if (alarm.dayOfWeek > 0) {
            val binaryStr = Integer.toBinaryString(alarm.dayOfWeek)
            val sb1 = StringBuilder()
            val sb2 = StringBuilder()
            for (i in 0 until (8 - binaryStr.length)) {
                sb1.append("0")
            }
            sb1.append(binaryStr)
            for (i in 0 until sb1.toString().length - 1) {
                if (TextUtils.equals("1", sb1.toString().reversed()[i].toString())) {
                    if (i == 6) {
                        sb2.append(dayOfWeek[0].drop(1)).append(" ")
                    } else {
                        sb2.append(dayOfWeek[i + 1].drop(1)).append(" ")
                    }
                }
            }
            mViewDataBinding.tvAlarmSettingRepeatValue.text = sb2.toString().dropLastWhile { TextUtils.equals(" ", it.toString()) }
        }
        when (alarm.type) {
            1 -> mViewDataBinding.sbAlarmSettingVoice.isChecked = true
            2 -> mViewDataBinding.sbAlarmSettingLighting.isChecked = true
            3 -> {
                mViewDataBinding.sbAlarmSettingVoice.isChecked = true
                mViewDataBinding.sbAlarmSettingLighting.isChecked = true
            }
        }

        Log.d("aa","--"+alarm.hour+"--"+alarm.minute)
        mViewDataBinding.tsvAlarmSetting.setTime(alarm.hour, alarm.minute)
        mViewDataBinding.sbAlarmSettingVoice.setOnCheckedChangeListener(this)
        mViewDataBinding.sbAlarmSettingLighting.setOnCheckedChangeListener(this)
        deviceMac?.let { controller.setAlarmType(it, alarm) }
    }

    override fun onStart() {
        super.onStart()
        val bleLampFragment = parentFragment.parentFragment as BleLampFragment
        bleLampFragment.hideBottomNavigationView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewDataBinding.sbAlarmSettingVoice.setOnCheckedChangeListener(null)
        mViewDataBinding.sbAlarmSettingLighting.setOnCheckedChangeListener(null)
    }


    override fun onClick(v: View?) {
        val ish = parentFragment as IFragmentStackHolder
        when (v?.id) {
            R.id.rl_alarm_setting_repeat -> {
                alarmViewModel?.setTime(mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute())
                ish.replaceFragment(R.id.inner_frag_control_container, AlarmDaySettingFragment().newInstance())
            }
            R.id.rl_alarm_setting_ring -> {
                alarmViewModel?.setTime(mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute())
                ish.replaceFragment(R.id.inner_frag_control_container, AlarmRingSettingFragment().newInstance())
            }
        }
    }

    override fun onCheckedChanged(view: SwitchButton, isChecked: Boolean) {
        if (view.id == R.id.sb_alarm_setting_voice) {
            if (mViewDataBinding.sbAlarmSettingLighting.isChecked) {
                if (isChecked) alarmViewModel?.setType(3, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute()) else alarmViewModel?.setType(2, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute())
            } else {
                if (isChecked) alarmViewModel?.setType(1, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute()) else alarmViewModel?.setType(0, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute())
            }
        } else if (view.id == R.id.sb_alarm_setting_lighting) {
            if (mViewDataBinding.sbAlarmSettingVoice.isChecked) {
                if (isChecked) alarmViewModel?.setType(3, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute()) else alarmViewModel?.setType(1, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute())
            } else {
                if (isChecked) alarmViewModel?.setType(2, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute()) else alarmViewModel?.setType(0, mViewDataBinding.tsvAlarmSetting.getCurrentHour(), mViewDataBinding.tsvAlarmSetting.getCurrentMinute())
            }
        }
    }

    private fun saveAlarm() {
        if (alarmViewModel?.getAlarm() != null && deviceId != -1) {
            val alarm = alarmViewModel?.getAlarm()!!.value
            if (alarm != null) {
                alarm.hour = mViewDataBinding.tsvAlarmSetting.getCurrentHour()
                alarm.minute = mViewDataBinding.tsvAlarmSetting.getCurrentMinute()
                if (alarm.deviceId == -1) {
                    alarm.deviceId = deviceId
                    alarmViewModel?.saveAlarm(alarm)
                } else {
                    alarmViewModel?.updateAlarm(alarm)
                }
                deviceMac?.let { controller.setAlarm(it, alarm)}
            }
        }
        activity.onBackPressed()
    }

}