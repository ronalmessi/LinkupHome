package com.ihomey.linkuphome.listener

import androidx.fragment.app.Fragment

/**
 * Created by dongcaizheng on 2018/4/17.
 */
interface IFragmentStackHolder {

    fun replaceFragment(containerId: Int, frag: Fragment)


}