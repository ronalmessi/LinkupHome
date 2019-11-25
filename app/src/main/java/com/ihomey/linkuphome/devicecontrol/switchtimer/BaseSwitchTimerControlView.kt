package com.ihomey.linkuphome.devicecontrol.switchtimer


import android.view.View
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.devicecontrol.switchtimer.timersetting.TimerSettingListener
import com.ihomey.linkuphome.widget.RadioGroupPlus

abstract class BaseSwitchTimerControlView(parentView: View):TimerSettingListener {

    protected val radioGroupPlus = parentView.findViewById<RadioGroupPlus>(R.id.rg_timer_setting)

    protected var mLocalState: LocalState = LocalState("0")

    abstract fun bindTo(localState: LocalState?)

    private var listener:OnTimerChangedListener?=null

    fun setOnTimerChangedListener(listener:OnTimerChangedListener?){
        this.listener=listener
    }

    fun getOnTimerChangedListener():OnTimerChangedListener?{
        return listener
    }

}


