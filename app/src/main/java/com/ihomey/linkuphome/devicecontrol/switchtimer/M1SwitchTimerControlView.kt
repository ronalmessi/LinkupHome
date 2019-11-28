package com.ihomey.linkuphome.devicecontrol.switchtimer

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewStub
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DayOfWeekListAdapter
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.devicecontrol.controller.RepeatTimerController
import com.ihomey.linkuphome.devicecontrol.controller.repeatcontroller.RepeatTimerControllerFactory
import com.ihomey.linkuphome.widget.DividerItemDecoration
import java.util.*

class M1SwitchTimerControlView(val parentView: View, val device: Device) : BaseSwitchTimerControlView(parentView), BaseQuickAdapter.OnItemClickListener {

    private var dayOfWeekListAdapter: DayOfWeekListAdapter = DayOfWeekListAdapter(false, R.layout.item_day_of_week_timer)
    private lateinit var dayOfWeekView: RecyclerView
    private lateinit var controller: RepeatTimerController

    override fun saveTime(hour: Int, minute: Int, isOpenTimer: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val dayOfWeekHBinaryStr = StringBuilder("0000000")
        val selectedDays = dayOfWeekListAdapter.selectedItems
        for (i in 0 until selectedDays.size()) {
            val key = selectedDays.keyAt(i)
            dayOfWeekHBinaryStr.replace(key, key + 1, "1")
        }
        val dayOfWeekValue = Integer.parseInt(dayOfWeekHBinaryStr.toString(), 2)
        if (isOpenTimer) {
            mLocalState.openTimer = calendar.timeInMillis
            mLocalState.openTimerOn = 1
            mLocalState.openDayOfWeek = dayOfWeekValue
//            controller.setRepeatTimer(minute, hour, isOpenTimer = true, isOn = true, repeatMode = dayOfWeekValue)
        } else {
            mLocalState.closeTimer = calendar.timeInMillis
            mLocalState.closeTimerOn = 1
            mLocalState.closeDayOfWeek = dayOfWeekValue
//            controller.setRepeatTimer(minute, hour, isOpenTimer = false, isOn = true, repeatMode = dayOfWeekValue)
        }
        getOnTimerChangedListener()?.onTimerChanged(mLocalState)
    }

    override fun setTimerEditable(isEditable: Boolean, isOpenTimer: Boolean) {
        radioGroupPlus.visibility = if (isEditable) View.GONE else View.VISIBLE
        dayOfWeekView.visibility = if (isEditable) View.VISIBLE else View.GONE
        if (isEditable) {
            val dayOfWeekHexStr = if (isOpenTimer) Integer.toBinaryString(mLocalState.openDayOfWeek) else Integer.toBinaryString(mLocalState.closeDayOfWeek)
            for (i in dayOfWeekHexStr.indices) {
                if (TextUtils.equals("1", dayOfWeekHexStr[i].toString())) {
                    dayOfWeekListAdapter.setItemSelected(7 - dayOfWeekHexStr.length + i, true)
                }
            }

        } else {
            dayOfWeekListAdapter.clearSelectedItems()
        }
    }

    override fun onSwitchStateChange(isOn: Boolean, isOpenTimer: Boolean) {
        if (isOpenTimer) {
            mLocalState.openTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.openTimer)
            controller.setRepeatTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), true, isOn, mLocalState.openDayOfWeek)
        } else {
            mLocalState.closeTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.closeTimer)
            controller.setRepeatTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), false, isOn, mLocalState.closeDayOfWeek)
        }
        getOnTimerChangedListener()?.onTimerChanged(mLocalState)
    }

    override fun bindTo(localState: LocalState?) {
        localState?.let { this.mLocalState = it }
        this.controller = RepeatTimerControllerFactory().createController(device)
        controller.syncTime()
        val viewStub=parentView.findViewById<ViewStub>(R.id.view_stub_dayofweek)
        if(viewStub!=null){
            val view = viewStub.inflate()
            dayOfWeekView = view.findViewById(R.id.rcv_daysOfWeek)
            (dayOfWeekView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            dayOfWeekView.layoutManager = LinearLayoutManager(parentView.context)
            parentView.context?.resources?.getDimension(R.dimen._2sdp)?.toInt()?.let { DividerItemDecoration(LinearLayoutManager.HORIZONTAL, 0, it, Color.parseColor("#EFEFF0"), true) }?.let { dayOfWeekView.addItemDecoration(it) }
            dayOfWeekView.adapter = dayOfWeekListAdapter
            dayOfWeekListAdapter.onItemClickListener = this
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        dayOfWeekListAdapter.setItemSelected(position, !dayOfWeekListAdapter.isItemSelected(position))
    }

}