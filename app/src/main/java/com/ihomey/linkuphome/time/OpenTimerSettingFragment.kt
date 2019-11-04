package com.ihomey.linkuphome.time

import android.graphics.Color
import android.os.Bundle
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
import com.ihomey.linkuphome.getHourList
import com.ihomey.linkuphome.getMinuteList
import com.ihomey.linkuphome.listener.TimerSettingListener
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.open_timer_setting_fragment.*
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class OpenTimerSettingFragment : BaseFragment(), SwitchButton.OnCheckedChangeListener {

    private lateinit var listener: TimerSettingListener

    fun setTimerSettingListener(listener: TimerSettingListener) {
        this.listener = listener
    }

    override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
        listener.updateTimerOnState(isChecked)
        frameLayout_timer_setting.isActivated = isChecked
    }

    protected lateinit var mViewModel: SceneSettingViewModel

    fun newInstance(): OpenTimerSettingFragment {
        return OpenTimerSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.open_timer_setting_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wheel_timer_hour.data = getHourList()
        wheel_timer_minute.data = getMinuteList()
        wheel_timer_hour.setCircleColor(Color.parseColor("#bbF48479"))
        wheel_timer_minute.setCircleColor(Color.parseColor("#bbF48479"))
        btn_timer_setting_edit.setOnClickListener {
            if (it.tag != null && it.tag as Boolean) {
                it.tag = null
                listener.saveTime(wheel_timer_hour.currentItemPosition, wheel_timer_minute.currentItemPosition)
                enableEditTimer(false)
                (it as Button).setText(R.string.action_edit)
            } else {
                it.tag = true
                enableEditTimer(true)
                (it as Button).setText(R.string.action_save)
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(parentFragment!!).get(SceneSettingViewModel::class.java)
        mViewModel.mCurrentLocalState.observe(this, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS && it.data != null && it.data.openTimer != 0L) {
                val calendar = Calendar.getInstance()
                calendar.time = Date(it.data.openTimer)
                wheel_timer_hour.selectedItemPosition = calendar.get(Calendar.HOUR_OF_DAY)
                wheel_timer_minute.selectedItemPosition = calendar.get(Calendar.MINUTE)
                switch_button_timer.isChecked = it.data.openTimerOn == 1
                frameLayout_timer_setting.isActivated = it.data.openTimerOn == 1
                switch_button_timer.postDelayed(runnable, 1000)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        switch_button_timer.removeCallbacks(runnable)
    }

    private val runnable = Runnable {
        switch_button_timer.setOnCheckedChangeListener(this)
    }

    private fun enableEditTimer(flag: Boolean) {
        wheel_timer_hour.setEditable(flag)
        wheel_timer_minute.setEditable(flag)
        switch_button_timer.visibility = if (flag) View.INVISIBLE else View.VISIBLE
        frameLayout_timer_setting.isActivated = !flag
        listener.enableEditTimer(flag)
    }
}

