package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseNavHostFragment


class HomePageAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> BaseNavHostFragment().newInstance(R.navigation.nav_device)
            1 -> BaseNavHostFragment().newInstance(R.navigation.nav_zone)
            2 -> BaseNavHostFragment().newInstance(R.navigation.nav_setting)
            else -> null
        }
    }
}