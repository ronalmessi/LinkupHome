package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import android.widget.ImageButton
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device


class SceneControlViewHolder(parentView: View) :View.OnClickListener {

    private val sceneBtn = parentView.findViewById<ImageButton>(R.id.btn_device_scene_setting)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        sceneBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.let { Navigation.findNavController(it).navigate(R.id.action_deviceControlFragment_to_sceneControlFragment)}
    }
}