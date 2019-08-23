package com.ihomey.linkuphome.adapter


import android.text.TextUtils
import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.widget.InfoTextLayout
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.alarm_setting_fragment.*
import java.lang.StringBuilder
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class AlarmListAdapter(layoutId: Int) : BaseQuickAdapter<Alarm, BaseViewHolder>(layoutId) {

    var listener:AlarmStateListener?=null

    override fun convert(helper: BaseViewHolder, item: Alarm) {
        val hour = if (item.hour > 11) item.hour - 12 else item.hour
        val hourStr = if (hour > 9) "" + hour else "0$hour"
        val minuteStr = if (item.minute > 9) "" + item.minute else "0" + item.minute
        helper.setText(R.id.tv_alarm_time, "$hourStr:$minuteStr")
        helper.setText(R.id.tv_alarm_am_pm, if (item.hour in 0..11) R.string.title_am else R.string.title_pm)
        val dayOfWeeStringBuilder=StringBuilder()
        val dayOfWeekHexStr=Integer.toBinaryString(item.dayOfWeek)
        for (i in 0 until dayOfWeekHexStr.length) {
            if (TextUtils.equals("1", dayOfWeekHexStr[i].toString())) {
                dayOfWeeStringBuilder.append(mContext.resources.getString(AppConfig.DAY_OF_WEEK[(7-dayOfWeekHexStr.length+i)])).append("  ")
            }
        }
        helper.getView<InfoTextLayout>(R.id.infoTextLayout_alarm_repeat).setTextValue(dayOfWeeStringBuilder.toString().dropLast(2))
        helper.getView<InfoTextLayout>(R.id.infoTextLayout_alarm_ring).setTextValue(mContext.resources.getString(AppConfig.RING_LIST[item.ringType]))
        helper.getView<InfoTextLayout>(R.id.infoTextLayout_alarm_lighting).setTextValue(mContext.resources.getString( if(item.type>1) R.string.title_on else R.string.title_off))
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.swipeLayout)
        val swipeLayout=helper.getView<SwipeLayout>(R.id.swipeLayout)
        swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener{
            override fun onOpen(layout: SwipeLayout?) {
                swipeLayout.isClickable=false
            }

            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
            }

            override fun onStartOpen(layout: SwipeLayout?) {
                swipeLayout.isClickable=false
            }

            override fun onStartClose(layout: SwipeLayout?) {
            }

            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
            }

            override fun onClose(layout: SwipeLayout?) {
                swipeLayout.postDelayed({swipeLayout.isClickable=true},250)
            }
        })

        val alarmStateBtn=helper.getView<SwitchButton>(R.id.sb_alarm_state)
        alarmStateBtn.isChecked=item.isOn==1
        alarmStateBtn.setOnCheckedChangeListener { _, isChecked ->
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

