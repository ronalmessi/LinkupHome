package com.ihomey.linkuphome.time

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.controller.V2Controller
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getHourList
import com.ihomey.linkuphome.getMinuteList
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import com.ihomey.linkuphome.syncTime
import kotlinx.android.synthetic.main.fragment_timer_setting_v2.*
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class V2TimerSettingFragment : BaseFragment() {

    protected lateinit var mControlDevice: Device
    private var mLocalState: LocalState = LocalState("")
    private var controller: V2Controller = V2Controller()

    protected lateinit var viewModel: HomeActivityViewModel
    protected lateinit var mViewModel: SceneSettingViewModel
    protected lateinit var listener: MeshServiceStateListener

    private lateinit var openTimerBgDrawable: Drawable
    private lateinit var closeTimerBgDrawable: Drawable

    fun newInstance(): V2TimerSettingFragment {
        return V2TimerSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer_setting_v2, container, false)
    }

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
            if (listener.isMeshServiceConnected()) syncTime(mControlDevice.instructId)
            mViewModel.setCurrentDeviceId(it.id)
        })
        mViewModel.mCurrentLocalState.observe(viewLifecycleOwner, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS) {
                if (it.data != null) mLocalState = it.data
                updateViewData(mLocalState)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hourList = getHourList()
        val minuteList = getMinuteList()
        val wheelBgColor = Color.parseColor("#bbF48479")
        openTimerBgDrawable = resources.getDrawable(R.drawable.bg_timer_setting_on_v2)
        closeTimerBgDrawable = resources.getDrawable(R.drawable.bg_timer_setting_off_v2)

        wheel_timer_hour_open.data = hourList
        wheel_timer_minute_open.data = minuteList
        wheel_timer_hour_open.setCircleColor(wheelBgColor)
        wheel_timer_minute_open.setCircleColor(wheelBgColor)

        wheel_timer_hour_close.data = hourList
        wheel_timer_minute_close.data = minuteList
        wheel_timer_hour_close.setCircleColor(wheelBgColor)
        wheel_timer_minute_close.setCircleColor(wheelBgColor)

        tv_edit.setOnClickListener {
            if (it.tag != null) {
                it.tag = null
                (it as TextView).setText(R.string.action_edit)
                enableEditTimer(false)
                saveTime()
            } else {
                it.tag = true
                (it as TextView).setText(R.string.action_save)
                enableEditTimer(true)
            }
        }
        toolbar_back.setOnClickListener { activity?.onBackPressed() }
        tv_info.setOnClickListener {
            var currentLanguage = LocaleHelper.getLanguage(context)
            currentLanguage = when {
                TextUtils.equals("zh-rCN", currentLanguage) -> "zh"
                TextUtils.equals("zh-rTW", currentLanguage) -> "zh-Hant"
                TextUtils.equals("de", currentLanguage) -> "de"
                TextUtils.equals("fr", currentLanguage) -> "fr"
                else -> "en"
            }
            val bundle = Bundle()
            bundle.putString("sourceUrl", "http://app-docs.linkuphome.net/#/V2AboutTimer/$currentLanguage")
            bundle.putString("title", "定时循环")
            Navigation.findNavController(view).navigate(R.id.action_v2TimerSettingFragment_to_webViewFragment, bundle)
        }
        iv_question_mark.setOnClickListener {
            var currentLanguage = LocaleHelper.getLanguage(context)
            currentLanguage = when {
                TextUtils.equals("zh-rCN", currentLanguage) -> "zh"
                TextUtils.equals("zh-rTW", currentLanguage) -> "zh-Hant"
                TextUtils.equals("de", currentLanguage) -> "de"
                TextUtils.equals("fr", currentLanguage) -> "fr"
                else -> "en"
            }
            val bundle = Bundle()
            bundle.putString("sourceUrl", "http://app-docs.linkuphome.net/#/V2AboutTimer/$currentLanguage")
            bundle.putString("title", context?.getString(R.string.title_timer_cycle))
            Navigation.findNavController(view).navigate(R.id.action_v2TimerSettingFragment_to_webViewFragment, bundle)
        }
    }

    private fun saveTime() {
        val openCalendar = Calendar.getInstance()
        openCalendar.set(Calendar.HOUR_OF_DAY, wheel_timer_hour_open.currentItemPosition)
        openCalendar.set(Calendar.MINUTE, wheel_timer_minute_open.currentItemPosition)
        mLocalState.openTimer = openCalendar.timeInMillis
        mLocalState.openTimerOn = 1
        val closeCalendar = Calendar.getInstance()
        closeCalendar.set(Calendar.HOUR_OF_DAY, wheel_timer_hour_close.currentItemPosition)
        closeCalendar.set(Calendar.MINUTE, wheel_timer_minute_close.currentItemPosition)
        mLocalState.closeTimer = closeCalendar.timeInMillis
        mLocalState.closeTimerOn = 1
        if (listener.isMeshServiceConnected()) controller.setTimer(mControlDevice.instructId, wheel_timer_minute_open.currentItemPosition, wheel_timer_hour_open.currentItemPosition, wheel_timer_minute_close.currentItemPosition, wheel_timer_hour_close.currentItemPosition)

        updateDeviceLocalSate()
    }

    private fun updateDeviceLocalSate() {
        mControlDevice.let {
            mLocalState.id = it.id
            mViewModel.updateLocalState(mLocalState)
        }
    }

    fun updateViewData(localState: LocalState?) {
        localState?.let {
            if (it.openTimerOn == 1 && it.closeTimerOn == 1) {
                if (it.openTimer != 0L) {
                    val openCalendar = Calendar.getInstance()
                    openCalendar.time = Date(it.openTimer)
                    wheel_timer_hour_open.selectedItemPosition = openCalendar.get(Calendar.HOUR_OF_DAY)
                    wheel_timer_minute_open.selectedItemPosition = openCalendar.get(Calendar.MINUTE)
                } else {
                    wheel_timer_hour_open.selectedItemPosition = 0
                    wheel_timer_minute_open.selectedItemPosition = 0
                }
                if (it.closeTimer != 0L) {
                    val closeCalendar = Calendar.getInstance()
                    closeCalendar.time = Date(it.closeTimer)
                    wheel_timer_hour_close.selectedItemPosition = closeCalendar.get(Calendar.HOUR_OF_DAY)
                    wheel_timer_minute_close.selectedItemPosition = closeCalendar.get(Calendar.MINUTE)
                } else {
                    wheel_timer_hour_close.selectedItemPosition = 0
                    wheel_timer_minute_close.selectedItemPosition = 0
                }
                setShadowBackground(false)
            } else {
                setShadowBackground(true)
            }
        }
    }


    private fun enableEditTimer(flag: Boolean) {
        wheel_timer_hour_open.setEditable(flag)
        wheel_timer_minute_open.setEditable(flag)
        wheel_timer_hour_close.setEditable(flag)
        wheel_timer_minute_close.setEditable(flag)
        setShadowBackground(flag)
    }


    private fun setShadowBackground(flag: Boolean) {
        if (flag) {
            openTimerBgDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
            iv_open_time_setting.background = openTimerBgDrawable
            closeTimerBgDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
            iv_close_time_setting.background = closeTimerBgDrawable
        } else {
            openTimerBgDrawable.clearColorFilter()
            iv_open_time_setting.background = openTimerBgDrawable
            closeTimerBgDrawable.clearColorFilter()
            iv_close_time_setting.background = closeTimerBgDrawable
        }
    }
}