package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import android.widget.ImageButton
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation
import com.ihomey.linkuphome.widget.SingleSelectToggleGroup
import com.ihomey.linkuphome.widget.ToggleButtonGroup


class ColorControlViewHolder(private val parentView: View) :View.OnClickListener, ToggleButtonGroup.OnCheckedChangeListener {

    private val colorCycleBtn = parentView.findViewById<ImageButton>(R.id.btn_device_cycling)
    private val lightingModeBtn = parentView.findViewById<ImageButton>(R.id.btn_device_lighting)
    private val colorCycleSpeedToggleGroup = parentView.findViewById<SingleSelectToggleGroup>(R.id.device_cycling_sstg_speed)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        colorCycleBtn.setOnClickListener(this)
        lightingModeBtn.setOnClickListener(this)
        colorCycleSpeedToggleGroup.setOnCheckedChangeListener(this)
        parentView.setOnClickListener{ hideColorCycleSpeedToggleGroup(true) }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_device_lighting) {
            hideColorCycleSpeedToggleGroup(true)
            LightControllerFactory().createColorController(mDevice)?.setLightingMode()
        }else if(v?.id == R.id.btn_device_cycling){
            hideColorCycleSpeedToggleGroup(colorCycleSpeedToggleGroup.visibility == View.VISIBLE)
        }
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean) {
        LightControllerFactory().createColorController(mDevice)?.setCycleMode(position)
    }

    private fun hideColorCycleSpeedToggleGroup(isHide: Boolean) {
        if(isHide&&colorCycleSpeedToggleGroup.visibility==View.VISIBLE){
            colorCycleSpeedToggleGroup.animation = moveToViewBottomAnimation()
            colorCycleSpeedToggleGroup.visibility=View.GONE
        }else if(!isHide&&colorCycleSpeedToggleGroup.visibility==View.GONE){
            colorCycleSpeedToggleGroup.animation = moveToViewLocationAnimation()
            colorCycleSpeedToggleGroup.visibility=View.VISIBLE
        }
    }

}