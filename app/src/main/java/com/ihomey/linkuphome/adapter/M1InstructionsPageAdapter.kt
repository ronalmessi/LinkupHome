package com.ihomey.linkuphome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ihomey.linkuphome.m1.BluetoothPairHintFragment
import com.ihomey.linkuphome.m1.TurnOnBluetoothHintFragment
import com.ihomey.linkuphome.m1.TurnOnPowerHintFragment


class M1InstructionsPageAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> TurnOnPowerHintFragment().newInstance()
            1 -> TurnOnBluetoothHintFragment().newInstance()
            2 -> BluetoothPairHintFragment().newInstance()
            else -> null
        }
    }
}