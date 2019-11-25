package com.ihomey.linkuphome.devicecontrol.switchtimer.timersetting

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class TimerSettingAdapter(fragmentManager: FragmentManager, val listener: TimerSettingListener) : FragmentPagerAdapter(fragmentManager) {


    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> {
                val fragment = TimerSettingFragment().newInstance(0)
                fragment.setTimerSettingListener(listener)
                fragment
            }
            1 -> {
                val fragment = TimerSettingFragment().newInstance(1)
                fragment.setTimerSettingListener(listener)
                fragment
            }
            else -> null
        }
    }
}