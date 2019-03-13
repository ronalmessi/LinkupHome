package com.ihomey.linkuphome.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.HomePageAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.sha256
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = HomePageAdapter(childFragmentManager)
        viewPager.offscreenPageLimit = 3
        bottom_nav_view.setOnNavigationItemSelectedListener { item ->
            viewPager.currentItem = bottom_nav_view.menu.findItem(item.itemId).order
            true
        }
    }

    fun showBottomNavigationBar(isVisible: Boolean) {
        bottom_nav_view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}
