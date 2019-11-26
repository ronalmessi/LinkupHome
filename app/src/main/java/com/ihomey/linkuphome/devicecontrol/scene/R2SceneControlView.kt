package com.ihomey.linkuphome.devicecontrol.scene

import android.content.Context
import android.widget.RadioButton
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.widget.RadioGroupPlus


class R2SceneControlView(val layoutId:Int, val context:Context): BaseSceneControlView(context,layoutId) {

    override fun bindTo(localState: LocalState?) {
        val radioGroupPlus=getControlView().findViewById<RadioGroupPlus>(R.id.device_rgp_scene)
        when (localState?.sceneMode) {
            0 -> radioGroupPlus.check(R.id.rb_scene_read)
            1 -> radioGroupPlus.check(R.id.rb_scene_sunset)
            2 -> radioGroupPlus.check(R.id.rb_scene_rest)
            3 -> radioGroupPlus.check(R.id.rb_scene_spring)
            4 -> radioGroupPlus.check(R.id.rb_scene_rainforest)
        }
        radioGroupPlus.setOnCheckedChangeListener {group, checkedId -> group.findViewById<RadioButton>(checkedId).tag?.let {getOnSceneChangListener()?.onSceneChanged(Integer.valueOf(it as String)) } }
    }
}