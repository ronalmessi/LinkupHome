package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.listener.TimerSettingListener
import com.ihomey.linkuphome.time.CloseTimerSettingFragment
import com.ihomey.linkuphome.time.OpenTimerSettingFragment


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class TimerSettingAdapter(fragmentManager: FragmentManager,val listener:TimerSettingListener) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> {
                val fragment= OpenTimerSettingFragment().newInstance()
                fragment.setTimerSettingListener(listener)
                fragment
            }
            1 -> {
                val fragment= CloseTimerSettingFragment().newInstance()
                fragment.setTimerSettingListener(listener)
                fragment
            }
            else -> null
        }
    }
}