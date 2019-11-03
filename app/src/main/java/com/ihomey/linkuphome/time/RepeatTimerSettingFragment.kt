package com.ihomey.linkuphome.time

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.TimerSettingAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.TimerSettingListener
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import kotlinx.android.synthetic.main.timer_setting_repeat_fragment.*
import kotlinx.android.synthetic.main.timer_setting_repeat_fragment.rg_timer_setting
import kotlinx.android.synthetic.main.timer_setting_repeat_fragment.toolbar_back
import kotlinx.android.synthetic.main.timer_setting_repeat_fragment.viewPager
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class RepeatTimerSettingFragment : BaseFragment(),TimerSettingListener {

    protected lateinit var viewModel: HomeActivityViewModel
    protected lateinit var mViewModel: SceneSettingViewModel

    protected lateinit var mControlDevice: Device
    private var mLocalState: LocalState= LocalState("0")
    private var controller: Controller? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.timer_setting_repeat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = TimerSettingAdapter(childFragmentManager,this)
        toolbar_back.setOnClickListener {Navigation.findNavController(it).popBackStack()}
        rg_timer_setting.setOnCheckedChangeListener { _, checkedId ->
            viewPager.currentItem= if(checkedId==R.id.rb_timer_setting_on) 0 else 1
        }
        rg_timer_setting.check(R.id.rb_timer_setting_on)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(SceneSettingViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(this, Observer<Device> {
            mControlDevice = it
            controller = ControllerFactory().createController(it.type,TextUtils.equals("LinkupHome V1",it.name))
            mViewModel.setCurrentDeviceId(it.id)
            if(TextUtils.equals("LinkupHome V1",mControlDevice.name)) controller?.syncTime(it.instructId)
        })
        mViewModel.mCurrentLocalState.observe(this, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS) {
                it.data?.let {
                    mLocalState=it
                }
            }
        })
    }

    override fun saveTime(hour:Int,minute:Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 1970)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        if (viewPager.currentItem==0) {
            mLocalState.openTimer = calendar.timeInMillis
            mLocalState.openTimerOn = 1
            controller?.setRepeatTimer(mControlDevice.instructId,minute,hour, isOpenTimer = true, isOn = true, isRepeat = cb_timer_setting_repeat.isChecked)
        } else {
            mLocalState.closeTimerOn = 1
            mLocalState.closeTimer = calendar.timeInMillis
            controller?.setRepeatTimer(mControlDevice.instructId,minute,hour, isOpenTimer = false, isOn = true, isRepeat = cb_timer_setting_repeat.isChecked)
        }
        mControlDevice.let {
            mLocalState.id=it.id
            mViewModel.updateLocalState(mLocalState)
        }

    }

    override fun enableEditTimer(flag: Boolean) {
        rg_timer_setting.visibility = if (flag) View.GONE else View.VISIBLE
        cb_timer_setting_repeat.visibility = if (flag) View.VISIBLE else View.GONE
        if(isVisible){
            val calendar = Calendar.getInstance()
            calendar.time= Date(if (viewPager.currentItem==0) mLocalState.openTimer else mLocalState.closeTimer)
            cb_timer_setting_repeat.isChecked = calendar.get(Calendar.YEAR) == 1970
        }
    }


    override fun updateTimerOnState(isChecked: Boolean) {
        if (viewPager.currentItem==0) {
            mLocalState.openTimerOn = if(isChecked) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.openTimer)
            controller?.setRepeatTimer(mControlDevice.instructId,calendar.get(Calendar.MINUTE),calendar.get(Calendar.HOUR_OF_DAY),true,isChecked,cb_timer_setting_repeat.isChecked)
        } else {
            mLocalState.closeTimerOn =if(isChecked) 1 else 0
            val calendar = Calendar.getInstance()
            calendar.time = Date(mLocalState.closeTimer)
            controller?.setRepeatTimer(mControlDevice.instructId,calendar.get(Calendar.MINUTE),calendar.get(Calendar.HOUR_OF_DAY),false,isChecked,cb_timer_setting_repeat.isChecked)
        }
        mControlDevice.let {
            mLocalState.id=it.id
            mViewModel.updateLocalState(mLocalState)
        }
    }

}

