package com.ihomey.linkuphome.zone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.FragmentBackHandler
import kotlinx.android.synthetic.main.home_fragment.*


class ZoneNavHostFragment : BaseFragment(),FragmentBackHandler {

    fun newInstance(): ZoneNavHostFragment {
        return ZoneNavHostFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.zone_navhost_fragment, container, false)
    }

    fun showBottomNavigationBar(isVisible: Boolean) {
       if(parentFragment!=null&&parentFragment is HomeFragment){
           (parentFragment as HomeFragment).showBottomNavigationBar(isVisible)
       }
    }

    override fun onBackPressed(): Boolean {
        return true
    }

}
