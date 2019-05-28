package com.ihomey.linkuphome.time

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DayOfWeekListAdapter
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.databinding.FragmentTimerSettingM1Binding
import com.ihomey.linkuphome.widget.DividerItemDecoration
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class M1TimerSettingFragment1 : BaseTimerSettingFragment(), BaseQuickAdapter.OnItemClickListener {

    lateinit var mViewDataBinding: FragmentTimerSettingM1Binding

    private var dayOfWeekListAdapter: DayOfWeekListAdapter = DayOfWeekListAdapter(false, R.layout.item_day_of_week_timer)

    fun newInstance(): M1TimerSettingFragment1 {
        return M1TimerSettingFragment1()
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
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer_setting_m1, container, false)
        mViewDataBinding.wheelTimerHour.data = getHourList()
        mViewDataBinding.wheelTimerMinute.data = getMinuteList()
        mViewDataBinding.btnTimerSettingEdit.tag = true
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mViewDataBinding.rcvDaysOfWeek.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mViewDataBinding.rcvDaysOfWeek.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._2sdp)?.toInt()?.let { DividerItemDecoration(LinearLayoutManager.HORIZONTAL, 0,it, Color.parseColor("#EFEFF0"), true) }?.let { mViewDataBinding.rcvDaysOfWeek.addItemDecoration(it) }
        mViewDataBinding.rcvDaysOfWeek.adapter = dayOfWeekListAdapter
        dayOfWeekListAdapter.onItemClickListener = this
    }


    override fun updateViewData(localState: LocalState?) {
        localState?.let {
            if (it.openTimerOn > 1 || it.closeTimerOn > 1) {
                var alarmTime: Long? = null
                if (it.openTimerOn > 1) {
                    mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bbF48479"))
                    mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bbF48479"))
                    mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_on)
                    mViewDataBinding.rbTimerSettingOn.isChecked = true
                    alarmTime = it.openTimer

                    mViewDataBinding.switchButtonTimer.isChecked = it.openTimerOn == 3
                    mViewDataBinding.frameLayoutTimerSetting.isActivated =it.openTimerOn == 3

                } else {
                    mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bb949494"))
                    mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bb949494"))
                    mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_off)
                    mViewDataBinding.rbTimerSettingOff.isChecked = true
                    alarmTime = it.closeTimer

                    mViewDataBinding.switchButtonTimer.isChecked =it.closeTimerOn == 3
                    mViewDataBinding.frameLayoutTimerSetting.isActivated = it.closeTimerOn == 3
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
        mViewDataBinding.rgTimerSetting.visibility = if (flag) View.GONE else View.VISIBLE
        mViewDataBinding.rcvDaysOfWeek.visibility = if (flag) View.VISIBLE else View.GONE
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
//        return mViewDataBinding.cbTimerSettingRepeat.isChecked
        return true
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        updateAlarms(position, dayOfWeekListAdapter.isItemSelected(position))
        dayOfWeekListAdapter.setItemSelected(position, !dayOfWeekListAdapter.isItemSelected(position))
    }

    private fun updateAlarms(position: Int, itemSelected: Boolean) {
//        if (position == 0) {
//            sb?.replace(position + 1, position + 2, if (itemSelected) "0" else "1")
//        } else {
//            sb?.replace(8 - position, 9 - position, if (itemSelected) "0" else "1")
//        }
    }

}

