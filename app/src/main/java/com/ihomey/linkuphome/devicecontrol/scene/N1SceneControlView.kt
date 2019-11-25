package com.ihomey.linkuphome.devicecontrol.scene

import android.content.Context
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.devicecontrol.view.BaseControlView
import com.ihomey.linkuphome.devicecontrol.viewholder.ColorControlViewHolder
import com.ihomey.linkuphome.devicecontrol.viewholder.CommonControlViewHolder
import com.ihomey.linkuphome.devicecontrol.viewholder.RGBrControlViewHolder
import com.ihomey.linkuphome.widget.RadioGroupPlus


class N1SceneControlView(val layoutId:Int, val context:Context): BaseSceneControlView(context,layoutId) {

    override fun bindTo(localState: LocalState?) {
        val radioGroupPlus=getControlView().findViewById<RadioGroupPlus>(R.id.device_rgp_scene)
        when (localState?.sceneMode) {
            0 -> radioGroupPlus.check(R.id.rb_scene_spring)
            1 -> radioGroupPlus.check(R.id.rb_scene_rainforest)
            2 -> radioGroupPlus.check(R.id.rb_scene_sunset)
            3 -> radioGroupPlus.check(R.id.rb_scene_lighting)
        }
        radioGroupPlus.setOnCheckedChangeListener {group, checkedId -> group.findViewById<RadioButton>(checkedId).tag?.let {getOnSceneChangListener()?.onSceneChanged(Integer.valueOf(it as String)) } }
    }
}