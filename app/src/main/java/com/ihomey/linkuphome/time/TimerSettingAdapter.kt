package com.ihomey.linkuphome.time

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.time.TimerSettingListener
import com.ihomey.linkuphome.time.TimerSettingFragment1


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
                val fragment = TimerSettingFragment1().newInstance(0)
                fragment.setTimerSettingListener(listener)
                fragment
            }
            1 -> {
                val fragment = TimerSettingFragment1().newInstance(1)
                fragment.setTimerSettingListener(listener)
                fragment
            }
            else -> null
        }
    }
}