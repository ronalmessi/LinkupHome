package com.ihomey.linkuphome.time

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.AlarmSettingAdapter
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentTimerSettingBedBinding
import com.ihomey.linkuphome.databinding.FragmentTimerSettingRepeatBinding
import com.ihomey.linkuphome.dayOfWeek
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.jackandphantom.blurimage.BlurImage
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/15.
 */
open class BedTimerSettingFragment : BaseTimerSettingFragment(), BaseQuickAdapter.OnItemClickListener {

    private var sb: StringBuilder? = null

    lateinit var mViewDataBinding: FragmentTimerSettingBedBinding

    private var alarmSettingAdapter: AlarmSettingAdapter = AlarmSettingAdapter(false, R.layout.item_timer_setting_bed, dayOfWeek)

    fun newInstance(): BedTimerSettingFragment {
        return BedTimerSettingFragment()
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
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer_setting_bed, container, false)
        mViewDataBinding.wheelTimerHour.data = getHourList()
        mViewDataBinding.wheelTimerMinute.data = getMinuteList()
        mViewDataBinding.btnTimerSettingEdit.tag = true
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (mViewDataBinding.rcvDayList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mViewDataBinding.rcvDayList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.rcvDayList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL, 2, ContextCompat.getColor(context, R.color.control_tab_item_normal_color), true))
        mViewDataBinding.rcvDayList.adapter = alarmSettingAdapter
        alarmSettingAdapter.onItemClickListener = this
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        updateAlarms(position, alarmSettingAdapter.isItemSelected(position))
        alarmSettingAdapter.setItemSelected(position, !alarmSettingAdapter.isItemSelected(position))
    }

    private fun updateAlarms(position: Int, itemSelected: Boolean) {
        if (position == 0) {
            sb?.replace(position + 1, position + 2, if (itemSelected) "0" else "1")
        } else {
            sb?.replace(8 - position, 9 - position, if (itemSelected) "0" else "1")
        }
    }


    override fun updateViewData(controlDevice: ControlDevice) {
        alarmSettingAdapter.clearSelectedItems()
        val lightState = controlDevice.state
        if (lightState.openTimerOn > 1 || lightState.closeTimerOn > 1) {
            var alarmTime: Long? = null
            if (lightState.openTimerOn > 1) {
                mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bbF48479"))
                mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bbF48479"))
                mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_on)
                mViewDataBinding.rbTimerSettingOn.isChecked = true
                alarmTime = lightState.openTimer

                mViewDataBinding.switchButtonTimer.isChecked = lightState.openTimerOn == 3
                mViewDataBinding.frameLayoutTimerSetting.isActivated = lightState.openTimerOn == 3

                val binaryStr = Integer.toBinaryString(lightState.openDayOfWeek)
                sb = StringBuilder()
                for (i in 0 until (8 - binaryStr.length)) {
                    sb?.append("0")
                }
                sb?.append(binaryStr)


            } else {
                mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bb949494"))
                mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bb949494"))
                mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_off)
                mViewDataBinding.rbTimerSettingOff.isChecked = true
                alarmTime = lightState.closeTimer

                mViewDataBinding.switchButtonTimer.isChecked = lightState.closeTimerOn == 3
                mViewDataBinding.frameLayoutTimerSetting.isActivated = lightState.closeTimerOn == 3

                val binaryStr = Integer.toBinaryString(lightState.closeDayOfWeek)
                sb = StringBuilder()
                for (i in 0 until (8 - binaryStr.length)) {
                    sb?.append("0")
                }
                sb?.append(binaryStr)
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


            updateView(sb?.toString()!!)

        } else {
            mViewDataBinding.wheelTimerHour.setCircleColor(Color.parseColor("#bbF48479"))
            mViewDataBinding.wheelTimerMinute.setCircleColor(Color.parseColor("#bbF48479"))
            mViewDataBinding.frameLayoutTimerSetting.setBackgroundResource(R.drawable.bg_timer_setting_on)
            mViewDataBinding.btnTimerSettingEdit.visibility = View.INVISIBLE
            mViewDataBinding.switchButtonTimer.visibility = View.INVISIBLE
        }
        mViewDataBinding.rgTimerSetting.setOnCheckedChangeListener(this)
        mViewDataBinding.switchButtonTimer.setOnCheckedChangeListener(this)
        mViewDataBinding.btnTimerSettingEdit.setOnClickListener(this)
        mViewDataBinding.toolbarBack.setOnClickListener(this)
    }

    private fun updateView(binaryStr: String) {
        for (i in 1 until binaryStr.length) {
            if (TextUtils.equals("1", binaryStr[i].toString())) {
                if (i == 1) alarmSettingAdapter.selectedItems[0] = 0 else alarmSettingAdapter.selectedItems[8 - i] = 8 - i
            }
        }
        alarmSettingAdapter.notifyDataSetChanged()
    }

    override fun enableEditTimer(flag: Boolean) {
        mViewDataBinding.wheelTimerHour.setEditable(flag)
        mViewDataBinding.wheelTimerMinute.setEditable(flag)
        mViewDataBinding.rbTimerSettingOn.visibility = if (flag) View.GONE else View.VISIBLE
        mViewDataBinding.rbTimerSettingOff.visibility = if (flag) View.GONE else View.VISIBLE
        mViewDataBinding.rcvDayList.visibility = if (flag) View.VISIBLE else View.GONE
        mViewDataBinding.switchButtonTimer.visibility = if (flag) View.INVISIBLE else View.VISIBLE
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
        return false
    }

    override fun getDayOfWeek(): Int {
        return Integer.parseInt(sb.toString(), 2)
    }

}

