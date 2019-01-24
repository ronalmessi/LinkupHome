package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.control.ControlParentFragment
import com.ihomey.linkuphome.control.LawnControlFragment
import com.ihomey.linkuphome.device.MeshDeviceListFragment
import com.ihomey.linkuphome.group.GroupFragment

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class ControlPageAdapter(lampCategoryType: Int, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val type: Int = lampCategoryType

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> {
                when (type) {
                    0 -> LawnControlFragment().newInstance()
                    else -> ControlParentFragment().newInstance(type)
                }
            }
            1 -> MeshDeviceListFragment().newInstance(type)
            2 -> GroupFragment().newInstance(type)
            else -> null
        }
    }
}