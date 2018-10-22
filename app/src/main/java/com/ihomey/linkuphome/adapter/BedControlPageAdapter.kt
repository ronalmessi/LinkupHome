package com.ihomey.linkuphome.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.ihomey.linkuphome.control.BedControlRGBFragment
import com.ihomey.linkuphome.control.BedControlWarmColdFragment
import com.ihomey.linkuphome.control.ControlParentFragment
import com.ihomey.linkuphome.control.LawnControlFragment
import com.ihomey.linkuphome.device.BleDeviceListFragment
import com.ihomey.linkuphome.device.MeshDeviceListFragment
import com.ihomey.linkuphome.group.GroupFragment

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class BedControlPageAdapter(lampCategoryType: Int, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val type: Int = lampCategoryType

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> BedControlRGBFragment().newInstance()
            1 -> BedControlWarmColdFragment().newInstance()
            else -> null
        }
    }
}