package com.ihomey.linkuphome.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.Alarm
import com.ihomey.linkuphome.dayOfWeek
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class AlarmListAdapter(data: MutableList<Alarm>?) : BaseMultiItemQuickAdapter<Alarm, BaseViewHolder>(data) {

    var listener:AlarmStateListener?=null

    init {
        addItemType(1, R.layout.item_alarm_list)
        addItemType(-1, R.layout.item_alarm_add)
    }

    override fun convert(helper: BaseViewHolder, item: Alarm) {
        when (helper.itemViewType) {
            1 -> {
                if (item.dayOfWeek > 0) {
                    val binaryStr = Integer.toBinaryString(item.dayOfWeek)
                    val sb1 = StringBuilder()
                    val sb2 = StringBuilder()
                    for (i in 0 until (8 - binaryStr.length)) {
                        sb1.append("0")
                    }
                    sb1.append(binaryStr)
                    for (i in 0 until sb1.toString().length - 1) {
                        if (TextUtils.equals("1", sb1.toString().reversed()[i].toString())) {
                            if (i == 6) {
                                sb2.append(dayOfWeek[0].drop(1)).append(" ")
                            } else {
                                sb2.append(dayOfWeek[i + 1].drop(1)).append(" ")
                            }
                        }
                    }
                    helper.setText(R.id.tv_alarm_day, sb2.toString().dropLastWhile { TextUtils.equals(" ", it.toString()) })
                } else {
                    helper.setText(R.id.tv_alarm_day, " ")
                }
                helper.setChecked(R.id.sb_alarm_state, item.isOn == 1)
                val hour = if (item.hour > 9) "" + item.hour else "0" + item.hour
                val minute = if (item.minute > 9) "" + item.minute else "0" + item.minute
                helper.setText(R.id.tv_alarm_time, "$hour:$minute")
                helper.getView<SwitchButton>(R.id.sb_alarm_state).setOnCheckedChangeListener { view, isChecked ->
                    if(listener!=null){
                        listener?.onStateChanged(isChecked,helper.adapterPosition)
                    }
                }
            }
        }
    }

    interface AlarmStateListener{
        fun onStateChanged(isOn:Boolean,position:Int)
    }

    public fun setAlarmStateListener(listener:AlarmStateListener?){
        this.listener=listener
    }

}