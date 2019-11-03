package com.ihomey.linkuphome.time

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import com.ihomey.linkuphome.syncTime
import com.ihomey.linkuphome.widget.RadioGroupPlus
import com.suke.widget.SwitchButton
import java.util.*

abstract class BaseTimerSettingFragment : BaseFragment(), RadioGroupPlus.OnCheckedChangeListener, View.OnClickListener, SwitchButton.OnCheckedChangeListener {

    protected lateinit var mControlDevice: Device
    private var mLocalState: LocalState= LocalState("")
    private var controller: Controller? = null

    protected lateinit var viewModel: HomeActivityViewModel
    protected lateinit var mViewModel: SceneSettingViewModel
    protected lateinit var listener: MeshServiceStateListener

    abstract fun updateViewData(localState: LocalState?)

    abstract fun enableEditTimer(flag: Boolean)

    abstract fun getHour(): Int

    abstract fun getMinute(): Int

    abstract fun isOpenTimer(): Boolean

    abstract fun setTimerOn(flag: Boolean)

    abstract fun isTimerOn(): Boolean

    abstract fun isRepeat(): Boolean

    private var isUserTouch: Boolean=false


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(SceneSettingViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(viewLifecycleOwner, Observer<Device> {
            mControlDevice = it
            controller = ControllerFactory().createController(mControlDevice.type, TextUtils.equals("LinkupHome V1",mControlDevice.name))
            mViewModel.setCurrentDeviceId(it.id)
            if(TextUtils.equals("LinkupHome V1",mControlDevice.name)) controller?.syncTime(it.instructId)
        })
        mViewModel.mCurrentLocalState.observe(viewLifecycleOwner, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS) {
                if(it.data!=null) mLocalState = it.data
                if(!isUserTouch){
                    updateViewData(mLocalState)
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toolbar_back -> activity?.onBackPressed()
            R.id.btn_timer_setting_edit -> {
                if (v.tag != null && v.tag as Boolean) {
                    v.tag = null
                    (v as Button).setText(R.string.action_save)
                    enableEditTimer(true)
                    if ((mControlDevice.type == 6||mControlDevice.type == 10) && listener.isMeshServiceConnected()) {
                        syncTime(mControlDevice.instructId)
                    }
                } else {
                    v.tag = true
                    (v as Button).setText(R.string.action_edit)
                    enableEditTimer(false)
                    saveTime()
                }
            }
        }
    }

    private fun saveTime() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, getHour())
        calendar.set(Calendar.MINUTE, getMinute())
        if (mControlDevice.type == 6||mControlDevice.type == 10) {
            if (isRepeat()) calendar.set(Calendar.YEAR, 1970)
            if (isOpenTimer()) {
                mLocalState.openTimer = calendar.timeInMillis
                mLocalState.openTimerOn = 3
                if (listener.isMeshServiceConnected()) {
                    controller?.setRepeatTimer(mControlDevice.instructId, getMinute(), getHour(), true, isTimerOn(), isRepeat())
                }
            } else {
                mLocalState.closeTimer = calendar.timeInMillis
                mLocalState.closeTimerOn = 3
                if (listener.isMeshServiceConnected()) {
                    controller?.setRepeatTimer(mControlDevice.instructId, getMinute(), getHour(), false, isTimerOn(), isRepeat())
                }
            }
        } else {
            val isExpired = calendar.timeInMillis - System.currentTimeMillis() < 0
            if (isOpenTimer()) {
                mLocalState.openTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
                mLocalState.openTimerOn = 3
            } else {
                mLocalState.closeTimerOn = 3
                mLocalState.closeTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
            }
        }
        isUserTouch=true
        setTimerOn(true)
        updateDeviceLocalSate()
    }

    private fun updateDeviceLocalSate() {
        mControlDevice.let {
            mLocalState.id=it.id
            mViewModel.updateLocalState(mLocalState)
        }
    }


    override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
        isUserTouch=true
        setTimerOn(isChecked)
        if (isOpenTimer()) {
            mLocalState.openTimerOn = if (isChecked) 3 else 2
            if (isChecked) {
                if (listener.isMeshServiceConnected()) {
                    if (mControlDevice.type == 6||mControlDevice.type == 10) {
                        controller?.setRepeatTimer(mControlDevice.instructId, getMinute(), getHour(), true, true, isRepeat())
                    } else {
                        controller?.setTimer(mControlDevice.instructId, getPeriodMinute(getHour(), getMinute()), true)
                    }
                }
            } else {
                if (mControlDevice.type == 6||mControlDevice.type == 10) {
                    if (listener.isMeshServiceConnected()) {
                        controller?.setRepeatTimer(mControlDevice.instructId, getMinute(), getHour(), true, false, isRepeat())
                    }
                }
            }
        } else {
            mLocalState.closeTimerOn = if (isChecked) 3 else 2
            if (isChecked) {
                if (listener.isMeshServiceConnected()) {
                    if (mControlDevice.type == 6||mControlDevice.type == 10) {
                        controller?.setRepeatTimer(mControlDevice.instructId, getMinute(), getHour(), false, true, isRepeat())
                    } else {
                        controller?.setTimer(mControlDevice.instructId, getPeriodMinute(getHour(), getMinute()), false)
                    }
                }
            } else {
                if (mControlDevice.type == 6||mControlDevice.type == 10) {
                    if (listener.isMeshServiceConnected()) {
                        controller?.setRepeatTimer(mControlDevice.instructId, getMinute(), getHour(), false, false, isRepeat())
                    }
                }
            }
        }
        updateDeviceLocalSate()
    }

    override fun onCheckedChanged(group: RadioGroupPlus?, checkedId: Int) {
        isUserTouch=false
        val radioButtonId = group?.checkedRadioButtonId
        if (radioButtonId == R.id.rb_timer_setting_on) {
            mLocalState.openTimerOn = mLocalState.openTimerOn + 2
            if (mLocalState.closeTimerOn > 1) {
                mLocalState.closeTimerOn = mLocalState.closeTimerOn - 2
            }
        } else {
            mLocalState.closeTimerOn = mLocalState.closeTimerOn + 2
            if (mLocalState.openTimerOn > 1) {
                mLocalState.openTimerOn = mLocalState.openTimerOn - 2
            }
        }
        updateDeviceLocalSate()
    }


    private fun getPeriodMinute(selectHour: Int, selectMinute: Int): Int {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        return if (selectHour > currentHour || selectHour == currentHour && selectMinute >= currentMinute) {
            (selectHour - currentHour) * 60 + selectMinute - currentMinute
        } else {
            (selectHour - currentHour + 24) * 60 + selectMinute - currentMinute
        }
    }
}