package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.viewholder.*


class T1ControlView(val layoutId:Int, val context:Context, val fragment: Fragment): BaseControlView(context,layoutId) {

    override fun bindTo(device: Device) {
        val parentView=getControlView()
        CommonControlViewHolder(parentView).bindTo(device)
        ColorControlViewHolder(parentView).bindTo(device)
        MixControlViewHolder(parentView,fragment.childFragmentManager).bindTo(device)
        SceneControlViewHolder(parentView).bindTo(device)
        SwitchTimerControlViewHolder(parentView).bindTo(device)
    }
}