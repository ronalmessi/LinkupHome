package com.ihomey.linkuphome.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ihomey.linkuphome.device.DeviceConnectStep1Fragment
import com.ihomey.linkuphome.device.DeviceConnectStep2Fragment

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceConnectStepPageAdapter(val lampCategoryType: Int, private val isReConnect: Boolean, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val type: Int = lampCategoryType

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> DeviceConnectStep1Fragment().newInstance(type)
            1 -> DeviceConnectStep2Fragment().newInstance(type, isReConnect)
            else -> null
        }
    }
}