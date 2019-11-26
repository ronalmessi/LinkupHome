package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.viewholder.*


class M1ControlView(val layoutId:Int, val context:Context, val fragment: Fragment): BaseControlView(context,layoutId) {

    override fun bindTo(device: Device){
        val parentView=getControlView()
        CommonControlViewHolder(parentView).bindTo(device)
        ColorControlViewHolder(parentView).bindTo(device)
        SceneControlViewHolder(parentView).bindTo(device)
        MixControlViewHolder(parentView,fragment.childFragmentManager).bindTo(device)
        M1FeatureControlViewHolder(parentView).bindTo(device)

        val mViewGroup = parentView.parent as ViewGroup
        val settingBtn=mViewGroup.findViewById<ImageView>(R.id.iv_setting)
        settingBtn.visibility= View.VISIBLE
        settingBtn.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_deviceControlFragment_to_m1ControlSettingFragment)
        }
    }

}