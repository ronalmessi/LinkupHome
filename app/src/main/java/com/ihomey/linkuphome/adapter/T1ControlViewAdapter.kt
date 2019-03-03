package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.control.T1ControlCctFragment
import com.ihomey.linkuphome.control.T1ControlRgbFragment


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class T1ControlViewAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {


    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> T1ControlRgbFragment().newInstance()
            1 -> T1ControlCctFragment().newInstance()
            else -> null
        }
    }
}