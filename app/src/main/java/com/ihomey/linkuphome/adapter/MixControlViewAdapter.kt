package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.control.CctControlFragment
import com.ihomey.linkuphome.control.RgbControlFragment


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class MixControlViewAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> RgbControlFragment().newInstance()
            1 -> CctControlFragment().newInstance()
            else -> null
        }
    }
}