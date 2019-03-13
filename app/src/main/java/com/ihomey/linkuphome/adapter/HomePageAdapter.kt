package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.device1.DeviceNavHostFragment
import com.ihomey.linkuphome.setting.SettingNavHostFragment
import com.ihomey.linkuphome.zone.ZoneNavHostFragment


class HomePageAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> DeviceNavHostFragment().newInstance()
            1 -> ZoneNavHostFragment().newInstance()
            2 -> SettingNavHostFragment().newInstance()
            else -> null
        }
    }
}