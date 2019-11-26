package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ihomey.linkuphome.devicecontrol.view.RgbControlView
import com.ihomey.linkuphome.devicecontrol.view.WarmColdControlView


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class MixControlViewAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> RgbControlView().newInstance()
            1 -> WarmColdControlView().newInstance()
            else -> null
        }
    }
}