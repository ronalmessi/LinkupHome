package com.ihomey.linkuphome.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ihomey.linkuphome.bed.EnvironmentMonitorFragment
import com.ihomey.linkuphome.alarm.AlarmFragment
import com.ihomey.linkuphome.control.ControlParentFragment
import com.ihomey.linkuphome.device.BleDeviceListFragment

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class BleLampPageAdapter(lampCategoryType: Int, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val type: Int = lampCategoryType

    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> BleDeviceListFragment().newInstance(type)
            1 -> ControlParentFragment().newInstance(type)
            2 -> EnvironmentMonitorFragment().newInstance()
            3 -> AlarmFragment().newInstance()
            else -> null
        }
    }
}