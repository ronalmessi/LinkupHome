package com.ihomey.linkuphome.alarm

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.M1Controller
import com.ihomey.linkuphome.data.entity.Alarm

import kotlinx.android.synthetic.main.alarm_setting_fragment.*
import java.lang.StringBuilder

open class AlarmSettingFragment : BaseFragment() {

    companion object {
        fun newInstance() = AlarmSettingFragment()
    }

    protected lateinit var mViewModel: AlarmViewModel

    private val controller: M1Controller = M1Controller()

    private lateinit var mAlarm: Alarm

    private  var mInitialAlarm: Alarm?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(AlarmViewModel::class.java)
        mViewModel.mAlarm.observe(viewLifecycleOwner, Observer<Alarm> {
            if(mInitialAlarm==null)mInitialAlarm=Alarm(0,0,it.dayOfWeek,it.hour,it.minute,it.ringType,it.type,0)
            mAlarm = it
            tv_title.text = if (mAlarm.editMode != 0) "编辑闹钟" else "添加闹钟"
            updateViews(it)
        })
    }

    private fun updateViews(alarm: Alarm?) {
        alarm?.let {
            tsv_alarm_setting.setTime(it.hour, it.minute)
            infoTextLayout_alarm_setting_ring.setTextValue(AppConfig.RING_LIST[it.ringType])
            sb_light_mode.isChecked = it.type > 1
            val dayOfWeeStringBuilder = StringBuilder()
            val dayOfWeekHexStr = Integer.toBinaryString(it.dayOfWeek)
            for (i in 0 until dayOfWeekHexStr.length) {
                if (TextUtils.equals("1", dayOfWeekHexStr[i].toString())) {
                    dayOfWeeStringBuilder.append(AppConfig.DAY_OF_WEEK[(7 - dayOfWeekHexStr.length + i)]).append("  ")
                }
            }
            infoTextLayout_alarm_setting_repeat.setTextValue(dayOfWeeStringBuilder.toString().dropLast(2))
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        infoTextLayout_alarm_setting_repeat.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_alarmSettingFragment_to_alarmDayListFragment) }
        infoTextLayout_alarm_setting_ring.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_alarmSettingFragment_to_alarmRingListFragment) }
        sb_light_mode.setOnCheckedChangeListener { _, isChecked ->
            mAlarm.let {
                it.type = if (isChecked) (if(it.ringType>0) 3 else 2) else 1
                controller.setAlarmType(it)
            }
        }
        btn_save.setOnClickListener {
            mAlarm.let {
                val targetHour = tsv_alarm_setting.getCurrentHour()
                val targetMinute = tsv_alarm_setting.getCurrentMinute()
                it.hour = targetHour
                it.minute = targetMinute
                if(mAlarm.editMode != 0&&mInitialAlarm?.dayOfWeek==it.dayOfWeek&&mInitialAlarm?.hour==it.hour&&mInitialAlarm?.minute==it.minute&&mInitialAlarm?.ringType==it.ringType&&mInitialAlarm?.type==it.type){
                    Navigation.findNavController(btn_save).popBackStack()
                }else{
                    it.isOn = 1
                    controller.setAlarm(it)
                    mViewModel.saveAlarm(it)
                    Navigation.findNavController(btn_save).popBackStack()
                }
            }
        }
    }
}
