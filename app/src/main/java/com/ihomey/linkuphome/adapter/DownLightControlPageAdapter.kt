package com.ihomey.linkuphome.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ihomey.linkuphome.control.*

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DownLightControlPageAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {


    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> BedControlRGBFragment().newInstance()
            1 -> DownLightControlWarmColdFragment().newInstance()
            else -> null
        }
    }
}