package com.ihomey.linkuphome.switchtimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.switchtimer.controller.TimerController
import com.ihomey.linkuphome.switchtimer.controller.TimerControllerFactory
import com.ihomey.linkuphome.switchtimer.timersetting.TimerSettingAdapter
import com.ihomey.linkuphome.switchtimer.timersetting.TimerSettingListener
import kotlinx.android.synthetic.main.fragment_timer_setting.*

import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class TimerFragment : BaseFragment(), TimerSettingListener {

    protected lateinit var viewModel: HomeActivityViewModel
    protected lateinit var mViewModel: SwitchTimerViewModel

    protected lateinit var mControlDevice: Device
    private var mLocalState: LocalState = LocalState("0")
    private var controller: TimerController?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer_setting, container, false)
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(SwitchTimerViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(this, Observer<Device> {
            mControlDevice = it
            controller = TimerControllerFactory().createController(it)
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
        calendar.set(Calendar.YEAR, 1970)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val isExpired = calendar.timeInMillis - System.currentTimeMillis() < 0
        if (viewPager.currentItem == 0) {
            mLocalState.openTimer =(if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
            mLocalState.openTimerOn = 1
            controller?.setTimer(minute, hour, isOpenTimer = true, isOn = true)
        } else {
            mLocalState.closeTimerOn = 1
            mLocalState.closeTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
            controller?.setTimer(minute, hour, isOpenTimer = false, isOn = false)
        }
        mControlDevice.let {
            mLocalState.id = it.id
            mViewModel.updateLocalState(mLocalState)
        }
    }

    override fun setTimerEditable(isEditable: Boolean) {

    }

    override fun onSwitchStateChange(isOn: Boolean) {
        if (viewPager.currentItem == 0) {
            mLocalState.openTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.openTimer)
            controller?.setTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), isOpenTimer =true, isOn = true)
        } else {
            mLocalState.closeTimerOn = if (isOn) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.closeTimer)
            controller?.setTimer(calendar.get(Calendar.MINUTE), calendar.get(Calendar.HOUR_OF_DAY), isOpenTimer =false, isOn = false)
        }
        mControlDevice.let {
            mLocalState.id = it.id
            mViewModel.updateLocalState(mLocalState)
        }
    }
}

