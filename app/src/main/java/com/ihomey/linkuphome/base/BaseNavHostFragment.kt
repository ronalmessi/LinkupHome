package com.ihomey.linkuphome.base

import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.FragmentVisibleStateListener


abstract  class BaseNavHostFragment : BaseFragment(), FragmentBackHandler {

    private var listener: FragmentVisibleStateListener? = null

    var isVisibleToUser: Boolean = false


    fun showBottomNavigationBar(isVisible: Boolean) {
        if (parentFragment != null && parentFragment is HomeFragment) {
            (parentFragment as HomeFragment).showBottomNavigationBar(isVisible)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (listener != null) listener?.onFragmentVisibleStateChanged(isVisibleToUser)
    }

    override fun onBackPressed(): Boolean {
        return handleBackPress(childFragmentManager.fragments[0])
    }

    fun setFragmentVisibleStateListener(listener: FragmentVisibleStateListener) {
        this.listener = listener
    }

}
