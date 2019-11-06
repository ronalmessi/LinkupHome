package com.ihomey.linkuphome.switchtimer.timersetting

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.switchtimer.SwitchTimerViewModel
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.timer_setting_fragment1.*
import kotlinx.android.synthetic.main.timer_setting_fragment1.btn_timer_setting_edit
import kotlinx.android.synthetic.main.timer_setting_fragment1.switch_button_timer
import kotlinx.android.synthetic.main.timer_setting_fragment1.wheel_timer_hour
import kotlinx.android.synthetic.main.timer_setting_fragment1.wheel_timer_minute
import java.util.*
import kotlin.collections.ArrayList


class TimerSettingFragment : BaseFragment(), SwitchButton.OnCheckedChangeListener {

    private var timerType = 0
    private val bgColor = listOf("#bbF48479", "#bb949494")

    private lateinit var mViewModel: SwitchTimerViewModel
    private  var listener: TimerSettingListener?=null

    fun setTimerSettingListener(listener: TimerSettingListener) {
        this.listener = listener
    }

    fun newInstance(type: Int): TimerSettingFragment {
        val fragment = TimerSettingFragment()
        val bundle = Bundle()
        bundle.putInt("type", type)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.timer_setting_fragment1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("type", 0)?.let { timerType = it }
        initTimerView(timerType)
    }

    private fun initTimerView(timerType: Int) {
        rl_container.setBackgroundResource(if (timerType == 0) R.drawable.bg_timer_setting_on else R.drawable.bg_timer_setting_off)
        wheel_timer_hour.data = getHourList()
        wheel_timer_minute.data = getMinuteList()
        wheel_timer_hour.setCircleColor(Color.parseColor(bgColor[timerType]))
        wheel_timer_minute.setCircleColor(Color.parseColor(bgColor[timerType]))
        btn_timer_setting_edit.setOnClickListener {
            if (it.tag != null && it.tag as Boolean) {
                it.tag = null
                listener?.saveTime(wheel_timer_hour.currentItemPosition, wheel_timer_minute.currentItemPosition)
                (it as Button).setText(R.string.action_edit)
                setEditable(false)
            } else {
                it.tag = true
                (it as Button).setText(R.string.action_save)
                setEditable(true)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentFragment?.let {
            mViewModel = ViewModelProviders.of(it).get(SwitchTimerViewModel::class.java)
            mViewModel.mCurrentLocalState.observe(this, Observer<Resource<LocalState>> {
                if (it.status == Status.SUCCESS) {
                    Log.d("aa",it.data.toString())
                    it.data?.let {
                        val timer = if (timerType == 0) it.openTimer else it.closeTimer
                        if (timer != 0L) {
                            val calendar = Calendar.getInstance()
                            calendar.time = Date(timer)
                            Log.d("aa","---"+calendar.get(Calendar.HOUR_OF_DAY)+"---"+calendar.get(Calendar.MINUTE))
                            wheel_timer_hour.selectedItemPosition = calendar.get(Calendar.HOUR_OF_DAY)
                            wheel_timer_minute.selectedItemPosition = calendar.get(Calendar.MINUTE)
                        }
                        val isTimerOn = if (timerType == 0) it.openTimerOn == 1 else it.closeTimerOn == 1
                        switch_button_timer.isChecked = isTimerOn
//                        mViewDataBinding.switchButtonTimer.isChecked = System.currentTimeMillis() <= alarmTime
                        rl_container.isActivated = isTimerOn
                    }
                    switch_button_timer.postDelayed(runnable, 1200)
                }
            })
        }
    }

    override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
        listener?.onSwitchStateChange(isChecked)
        rl_container.isActivated = isChecked
    }

    override fun onDestroyView() {
        super.onDestroyView()
        switch_button_timer.removeCallbacks(runnable)
    }

    private val runnable = Runnable {
        switch_button_timer.setOnCheckedChangeListener(this)
    }

    private fun setEditable(isEditable: Boolean) {
        wheel_timer_hour.setEditable(isEditable)
        wheel_timer_minute.setEditable(isEditable)
        switch_button_timer.visibility = if (isEditable) View.INVISIBLE else View.VISIBLE
        rl_container.isActivated = !isEditable
        listener?.setTimerEditable(isEditable)
    }


    private fun getMinuteList(): ArrayList<String> {
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

    private fun getHourList(): ArrayList<String> {
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

}