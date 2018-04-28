package com.ihomey.linkuphome.time

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.DeviceInfo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.FragmentTimerSettingBinding
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.suke.widget.SwitchButton
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class TimerSettingFragment : BaseFragment(), RadioGroup.OnCheckedChangeListener, View.OnClickListener, SwitchButton.OnCheckedChangeListener {

    lateinit var mViewDataBinding: FragmentTimerSettingBinding
    protected var mViewModel: TimerSettingViewModel? = null
    private var controller: Controller? = null
    private var mControlDevice: ControlDevice? = null
    protected lateinit var listener: MeshServiceStateListener

    fun newInstance(deviceId: Int, deviceType: Int): TimerSettingFragment {
        val fragment = TimerSettingFragment()
        val bundle = Bundle()
        bundle.putInt("deviceId", deviceId)
        bundle.putInt("deviceType", deviceType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer_setting, container, false)
        controller = ControllerFactory().createController(arguments.getInt("deviceType"))
        mViewDataBinding.tvTimerSettingHourEdit.setLabel("h")
        mViewDataBinding.tvTimerSettingHourEdit.setTextSize(48f)
        mViewDataBinding.tvTimerSettingHourEdit.setDataList(getHourList())

        mViewDataBinding.tvTimerSettingMinuteEdit.setLabel("m");
        mViewDataBinding.tvTimerSettingMinuteEdit.setTextSize(48f)
        mViewDataBinding.tvTimerSettingMinuteEdit.setDataList(getMinuteList())

        mViewDataBinding.toolbarRight.setOnClickListener(this)
        mViewDataBinding.toolbarRight.tag = true

        mViewDataBinding.rgTimerSetting.setOnCheckedChangeListener(this)
        mViewDataBinding.switchButtonTimer.setOnCheckedChangeListener(this)

        mViewDataBinding.toolbarBack.setOnClickListener {
            if (parentFragment is IFragmentStackHolder) {
                val fsh = parentFragment as IFragmentStackHolder
                fsh.goBack()
            }
        }
        return mViewDataBinding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(TimerSettingViewModel::class.java)
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS) {
                mControlDevice = it.data
                val lightState = mControlDevice?.state
                if (lightState != null) {
                    if (lightState.openTimer != 0L) {
                        val calendar = Calendar.getInstance()
                        calendar.time = Date(lightState.openTimer)
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)
                        mViewDataBinding.tvTimerSettingHourEdit.setInitPosition(hour)
                        mViewDataBinding.tvTimerSettingMinuteEdit.setInitPosition(minute)
                    } else {
                        mViewDataBinding.tvTimerSettingHourEdit.setInitPosition(0)
                        mViewDataBinding.tvTimerSettingMinuteEdit.setInitPosition(0)
                    }
                    if (System.currentTimeMillis() > lightState.openTimer) {
                        mViewDataBinding.switchButtonTimer.isChecked = false
                    } else {
                        mViewDataBinding.switchButtonTimer.isChecked = lightState.openTimerOn == 1
                    }
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(arguments.getInt("deviceType", -1), arguments.getInt("deviceId", -1)))
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val radioButtonId = group?.checkedRadioButtonId
        if (radioButtonId == R.id.rb_timer_setting_on) {
            mViewDataBinding.tvTimerSettingType.setText(R.string.lightOpenTime)
            mViewDataBinding.tvTimerSettingHourEdit.setCircleColor(1)
            mViewDataBinding.tvTimerSettingMinuteEdit.setCircleColor(1)
            mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.mipmap.timer_setting_on_bg)

            val lightState = mControlDevice?.state
            if (lightState != null) {
                if (lightState.openTimer != 0L) {
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(lightState.openTimer)
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    mViewDataBinding.tvTimerSettingHourEdit.setInitPosition(hour)
                    mViewDataBinding.tvTimerSettingMinuteEdit.setInitPosition(minute)
                } else {
                    mViewDataBinding.tvTimerSettingHourEdit.setInitPosition(0)
                    mViewDataBinding.tvTimerSettingMinuteEdit.setInitPosition(0)
                }
                if (System.currentTimeMillis() > lightState.openTimer) {
                    mViewDataBinding.switchButtonTimer.isChecked = false
                } else {
                    mViewDataBinding.switchButtonTimer.isChecked = lightState.openTimerOn == 1
                }
            }
        } else {
            mViewDataBinding.tvTimerSettingType.setText(R.string.lightCloseTime)
            mViewDataBinding.tvTimerSettingHourEdit.setCircleColor(0)
            mViewDataBinding.tvTimerSettingMinuteEdit.setCircleColor(0)
            mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.mipmap.timer_setting_off_bg)

            val lightState = mControlDevice?.state
            if (lightState != null) {
                if (lightState.closeTimer != 0L) {
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(lightState.closeTimer)
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    mViewDataBinding.tvTimerSettingHourEdit.setInitPosition(hour)
                    mViewDataBinding.tvTimerSettingMinuteEdit.setInitPosition(minute)
                } else {
                    mViewDataBinding.tvTimerSettingHourEdit.setInitPosition(0)
                    mViewDataBinding.tvTimerSettingMinuteEdit.setInitPosition(0)
                }
                if (System.currentTimeMillis() > lightState.closeTimer) {
                    mViewDataBinding.switchButtonTimer.isChecked = false
                } else {
                    mViewDataBinding.switchButtonTimer.isChecked = lightState.closeTimerOn == 1
                }
            }
        }
    }

    override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
        val lightState = mControlDevice?.state
        if (lightState != null) {
            if (mViewDataBinding.rgTimerSetting.checkedRadioButtonId == R.id.rb_timer_setting_on) {
                if (isChecked) {
                    lightState.openTimerOn = 1
                    if (mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                        controller?.setTimer(mControlDevice?.id!!, getPeriodMinute(mViewDataBinding.tvTimerSettingHourEdit.selectedItem, mViewDataBinding.tvTimerSettingMinuteEdit.selectedItem), true)
                    }
                } else {
                    lightState.openTimerOn = 0
                }
            } else {
                if (isChecked) {
                    lightState.closeTimerOn = 1
                    if (mControlDevice != null && mControlDevice?.id != null && listener.isMeshServiceConnected()) {
                        controller?.setTimer(mControlDevice?.id!!, getPeriodMinute(mViewDataBinding.tvTimerSettingHourEdit.selectedItem, mViewDataBinding.tvTimerSettingMinuteEdit.selectedItem), false)
                    }
                } else {
                    lightState.closeTimerOn = 0
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.tag != null && v.tag as Boolean) {
            mViewDataBinding.tvTimerSettingMinuteEdit.setCanEdit(true)
            mViewDataBinding.tvTimerSettingHourEdit.setCanEdit(true)
            v.tag = null
            mViewDataBinding.toolbarRight.setText(R.string.save)
        } else {
            mViewDataBinding.tvTimerSettingMinuteEdit.setCanEdit(false)
            mViewDataBinding.tvTimerSettingHourEdit.setCanEdit(false)
            v?.tag = true
            mViewDataBinding.toolbarRight.setText(R.string.edit)
            val lightState = mControlDevice?.state
            if (lightState != null) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, mViewDataBinding.tvTimerSettingHourEdit.selectedItem)
                calendar.set(Calendar.MINUTE, mViewDataBinding.tvTimerSettingMinuteEdit.selectedItem)
                val isExpired = calendar.timeInMillis - System.currentTimeMillis() < 0
                if (mViewDataBinding.rgTimerSetting.checkedRadioButtonId == R.id.rb_timer_setting_on) {
                    lightState.openTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
                } else {
                    lightState.closeTimer = (if (isExpired) calendar.timeInMillis + 24 * 60 * 60 * 1000 else calendar.timeInMillis)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel?.updateDevice(mControlDevice)
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