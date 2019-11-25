package com.ihomey.linkuphome.alarm

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.devicecontrol.controller.impl.M1Controller
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.home.HomeActivityViewModel
import kotlinx.android.synthetic.main.alarm_setting_fragment.*

open class AlarmSettingFragment : BaseFragment() {

    companion object {
        fun newInstance() = AlarmSettingFragment()
    }

    protected lateinit var mViewModel: AlarmViewModel

    protected lateinit var viewModel: HomeActivityViewModel

    private val controller: M1Controller = M1Controller()

    private var mDevice: Device? = null

    private lateinit var mAlarm: Alarm

    private var mInitialAlarm: Alarm? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(viewLifecycleOwner, Observer<Device> {
            mDevice = it
        })
        mViewModel = ViewModelProviders.of(activity!!).get(AlarmViewModel::class.java)
        mViewModel.mAlarm.observe(viewLifecycleOwner, Observer<Alarm> {
            if (mInitialAlarm == null) mInitialAlarm = Alarm(0, "", it.dayOfWeek, it.hour, it.minute, it.ringType, it.type, 0)
            mAlarm = it
            tv_title.setText(if (mAlarm.editMode != 0) R.string.title_edit_alarm else R.string.title_add_alarm)
            updateViews(it)
        })
    }

    private fun updateViews(alarm: Alarm?) {
        alarm?.let { it0 ->
            tsv_alarm_setting.setTime(it0.hour, it0.minute)
            context?.resources?.let { infoTextLayout_alarm_setting_ring.setTextValue(it.getString(AppConfig.RING_LIST[it0.ringType])) }
            sb_light_mode.isChecked = it0.type > 1
            val dayOfWeeStringBuilder = StringBuilder()
            val dayOfWeekHexStr = Integer.toBinaryString(it0.dayOfWeek)
            for (i in 0 until dayOfWeekHexStr.length) {
                if (TextUtils.equals("1", dayOfWeekHexStr[i].toString())) {
                    context?.resources?.let { dayOfWeeStringBuilder.append(it.getString(AppConfig.DAY_OF_WEEK[(7 - dayOfWeekHexStr.length + i)])).append("  ") }

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
                it.type = if (isChecked) (if (it.ringType > 0) 3 else 2) else 1
                controller.setAlarmType(mDevice?.id, it)
            }
        }
        btn_save.setOnClickListener {
            mAlarm.let {
                val targetHour = tsv_alarm_setting.getCurrentHour()
                val targetMinute = tsv_alarm_setting.getCurrentMinute()
                it.hour = targetHour
                it.minute = targetMinute
                if (mAlarm.editMode != 0 && mInitialAlarm?.dayOfWeek == it.dayOfWeek && mInitialAlarm?.hour == it.hour && mInitialAlarm?.minute == it.minute && mInitialAlarm?.ringType == it.ringType && mInitialAlarm?.type == it.type) {
                    Navigation.findNavController(btn_save).popBackStack()
                } else {
                    it.isOn = 1
                    controller.setAlarm(mDevice?.id, it)
                    mViewModel.saveAlarm(it)
                    Navigation.findNavController(btn_save).popBackStack()
                }
            }
        }
    }
}
