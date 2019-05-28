package com.ihomey.linkuphome.adapter


import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Alarm
import com.suke.widget.SwitchButton
import java.lang.StringBuilder
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class AlarmListAdapter(layoutId: Int) : BaseQuickAdapter<Alarm, BaseViewHolder>(layoutId) {

    val DAY_OF_WEEK= listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    var listener:AlarmStateListener?=null

    override fun convert(helper: BaseViewHolder, item: Alarm) {

        if (item.dayOfWeek > 0) {
            val dayOfWeeStringBuilder = StringBuilder()
            val dayOfWeekHexStr = Integer.toBinaryString(item.dayOfWeek)
            for (i in 0 until dayOfWeekHexStr.length) {
                if (TextUtils.equals("1", dayOfWeekHexStr[i].toString())) {
                    dayOfWeeStringBuilder.append(DAY_OF_WEEK[(7 - dayOfWeekHexStr.length + i)]).append("  ")
                }
            }
            helper.setText(R.id.tv_alarm_dayOfWeek, dayOfWeeStringBuilder.toString().dropLast(2))
            helper.setChecked(R.id.sb_alarm_state, item.isOn == 1)
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, item.hour)
            calendar.set(Calendar.MINUTE, item.minute)
            helper.setChecked(R.id.sb_alarm_state, item.isOn == 1)
//            val isExpired = calendar.timeInMillis - System.currentTimeMillis() < 0
//            helper.setChecked(R.id.sb_alarm_state, (item.isOn == 1&&!isExpired))
        }
        val hour = if (item.hour > 11) item.hour - 12 else item.hour
        val hourStr = if (hour > 9) "" + hour else "0$hour"
        val minuteStr = if (item.minute > 9) "" + item.minute else "0" + item.minute
        helper.setText(R.id.tv_alarm_time, "$hourStr:$minuteStr")
        helper.setText(R.id.tv_alarm_am_pm, if (item.hour in 0..11) "上午" else "下午")
        helper.getView<SwitchButton>(R.id.sb_alarm_state).setOnCheckedChangeListener { view, isChecked ->
            if(listener!=null){
                listener?.onStateChanged(isChecked,item)
            }
        }
    }

    interface AlarmStateListener{
        fun onStateChanged(isOn:Boolean,item: Alarm)
    }

     fun setAlarmStateListener(listener:AlarmStateListener?){
        this.listener=listener
    }
}

