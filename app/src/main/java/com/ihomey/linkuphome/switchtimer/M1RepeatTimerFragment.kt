package com.ihomey.linkuphome.switchtimer

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DayOfWeekListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import com.ihomey.linkuphome.switchtimer.repeatcontroller.RepeatTimerController
import com.ihomey.linkuphome.switchtimer.repeatcontroller.RepeatTimerControllerFactory
import com.ihomey.linkuphome.switchtimer.timersetting.TimerSettingAdapter
import com.ihomey.linkuphome.switchtimer.timersetting.TimerSettingListener
import com.ihomey.linkuphome.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_timer_setting_m1.*
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class M1RepeatTimerFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener, TimerSettingListener {

    protected lateinit var viewModel: HomeActivityViewModel
    protected lateinit var mViewModel: SwitchTimerViewModel

    private var dayOfWeekListAdapter: DayOfWeekListAdapter = DayOfWeekListAdapter(false, R.layout.item_day_of_week_timer)

    protected lateinit var mControlDevice: Device
    private var mLocalState: LocalState = LocalState("0")
    private lateinit var controller: RepeatTimerController


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer_setting_m1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = TimerSettingAdapter(childFragmentManager, this)
        toolbar_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        rg_timer_setting.setOnCheckedChangeListener { _, checkedId ->
            viewPager.currentItem = if (checkedId == R.id.rb_timer_setting_on) 0 else 1
        }
        rg_timer_setting.check(R.id.rb_timer_setting_on)
        (rcv_daysOfWeek.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        rcv_daysOfWeek.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._2sdp)?.toInt()?.let { DividerItemDecoration(LinearLayoutManager.HORIZONTAL, 0, it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_daysOfWeek.addItemDecoration(it) }
        rcv_daysOfWeek.adapter = dayOfWeekListAdapter
        dayOfWeekListAdapter.onItemClickListener = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(SwitchTimerViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(this, Observer<Device> {
            mControlDevice = it
            controller = RepeatTimerControllerFactory().createController(it)
            controller.syncTime()
            mViewModel.setCurrentDeviceId(it.id)
        })
        mViewModel.mCurrentLocalState.observe(this, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS) {
                it.data?.let {
                    mLocalState = it
                }
            }
        })
    }

    override fun saveTime(hour: Int, minute: Int) {
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
        if (viewPager.currentItem == 0) {
            mLocalState.openTimer = calendar.timeInMillis
            mLocalState.openTimerOn = 1
            mLocalState.openDayOfWeek = dayOfWeekValue
            controller.setRepeatTimer(minute, hour, isOpenTimer = true, isOn = true, repeatMode = dayOfWeekValue)
        } else {
            mLocalState.closeTimerOn = 1
            mLocalState.closeTimer = calendar.timeInMillis
            mLocalState.closeDayOfWeek = dayOfWeekValue
            controller.setRepeatTimer( minute, hour, isOpenTimer = false, isOn = true, repeatMode = dayOfWeekValue)
        }
        mControlDevice.let {
            mLocalState.id = it.id
            mViewModel.updateLocalState(mLocalState)
        }
    }

    override fun setTimerEditable(isEditable: Boolean) {
        rg_timer_setting.visibility = if (isEditable) View.GONE else View.VISIBLE
        rcv_daysOfWeek.visibility = if (isEditable) View.VISIBLE else View.GONE
        if (isEditable) {
            val dayOfWeekHexStr = if (viewPager.currentItem == 0) Integer.toBinaryString(mLocalState.openDayOfWeek) else Integer.toBinaryString(mLocalState.closeDayOfWeek)
            for (i in dayOfWeekHexStr.indices) {
                if (TextUtils.equals("1", dayOfWeekHexStr[i].toString())) {
                    dayOfWeekListAdapter.setItemSelected(7 - dayOfWeekHexStr.length + i, true)
                }
            }
        } else {
            dayOfWeekListAdapter.clearSelectedItems()
        }
    }


    override fun onSwitchStateChange(isChecked: Boolean) {
        if (viewPager.currentItem == 0) {
            mLocalState.openTimerOn = if (isChecked) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.openTimer)
            controller.setRepeatTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), true, isChecked, mLocalState.openDayOfWeek)
        } else {
            mLocalState.closeTimerOn = if (isChecked) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.closeTimer)
            controller.setRepeatTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), false, isChecked, mLocalState.openDayOfWeek)
        }
        mControlDevice.let {
            mLocalState.id = it.id
            mViewModel.updateLocalState(mLocalState)
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        dayOfWeekListAdapter.setItemSelected(position, !dayOfWeekListAdapter.isItemSelected(position))
    }

}
