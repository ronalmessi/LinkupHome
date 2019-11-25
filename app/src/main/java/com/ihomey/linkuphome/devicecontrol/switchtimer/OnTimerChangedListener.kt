package com.ihomey.linkuphome.devicecontrol.switchtimer

import com.ihomey.linkuphome.data.entity.LocalState


interface OnTimerChangedListener {
    fun onTimerChanged(localState: LocalState)
}