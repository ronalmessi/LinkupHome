package com.ihomey.linkuphome.devicecontrol.viewholder

import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.FragmentManager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.MixControlViewAdapter
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.widget.NoScrollViewpager


class MixControlViewHolder(parentView: View,val fragmentManager:FragmentManager) :RadioGroup.OnCheckedChangeListener {

    private val viewPager = parentView.findViewById<NoScrollViewpager>(R.id.viewPager)
    private val switchRg = parentView.findViewById<RadioGroup>(R.id.rg_control_switch)

    private lateinit var mDevice: Device

    fun bindTo(device: Device) {
        this.mDevice = device
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = MixControlViewAdapter(fragmentManager)
        switchRg.setOnCheckedChangeListener(this)
    }
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == R.id.rb_control_rgb) {
            viewPager.setCurrentItem(0, true)
        } else if (checkedId == R.id.rb_control_warm_cold) {
            viewPager.setCurrentItem(1, true)
        }
    }
}