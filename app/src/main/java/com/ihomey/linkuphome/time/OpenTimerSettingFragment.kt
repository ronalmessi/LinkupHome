package com.ihomey.linkuphome.time

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
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import kotlinx.android.synthetic.main.open_timer_setting_fragment.*
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class OpenTimerSettingFragment : BaseFragment(){

    protected lateinit var mViewModel: SceneSettingViewModel

    fun newInstance(): OpenTimerSettingFragment {
        return OpenTimerSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate( R.layout.open_timer_setting_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wheel_timer_hour.data=getHourList()
        wheel_timer_minute.data=getMinuteList()
        wheel_timer_hour.setCircleColor(Color.parseColor("#bbF48479"))
        wheel_timer_minute.setCircleColor(Color.parseColor("#bbF48479"))
        btn_timer_setting_edit.setOnClickListener {
            if (it.tag != null && it.tag as Boolean) {
                it.tag = null
                enableEditTimer(false)
                saveTime()
                (it as Button).setText(R.string.action_edit)
            } else {
                it.tag = true
                enableEditTimer(true)
                (it as Button).setText(R.string.action_save)
            }
        }
        switch_button_timer.postDelayed({
            switch_button_timer.setOnCheckedChangeListener { _, isChecked ->
                updateTimerOnState(isChecked)
                frameLayout_timer_setting.isActivated=isChecked
            }
        },1500)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentFragment?.let {
            mViewModel = ViewModelProviders.of(it).get(SceneSettingViewModel::class.java)
            mViewModel.mCurrentLocalState.observe(this, Observer<Resource<LocalState>> {
                if (it.status == Status.SUCCESS) {
                    it.data?.let {
                        val calendar = Calendar.getInstance()
                        calendar.time = Date(it.openTimer)
                        wheel_timer_hour.selectedItemPosition = calendar.get(Calendar.HOUR_OF_DAY)
                        wheel_timer_minute.selectedItemPosition = calendar.get(Calendar.MINUTE)
                        switch_button_timer.isChecked = it.openTimerOn==1
                    }
                }
            })
        }
    }

    private fun saveTime(){
        parentFragment.let {
            val timerSettingFragment=it as M1TimerSettingFragment
            timerSettingFragment.saveTime(wheel_timer_hour.currentItemPosition,wheel_timer_minute.currentItemPosition)
        }
    }

    private fun enableEditTimer(flag: Boolean) {
        wheel_timer_hour.setEditable(flag)
        wheel_timer_minute.setEditable(flag)
        switch_button_timer.visibility = if (flag) View.INVISIBLE else View.VISIBLE
        if(flag) switch_button_timer.isChecked=false
        parentFragment.let {
            val timerSettingFragment=it as M1TimerSettingFragment
            timerSettingFragment.showDayOfWeeks(flag)
        }
    }

    private fun updateTimerOnState(isChecked: Boolean) {
        parentFragment.let {
            val timerSettingFragment=it as M1TimerSettingFragment
            timerSettingFragment.updateTimerOnState(isChecked)
        }
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

