package com.ihomey.linkuphome.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.HomePageAdapter
import com.ihomey.linkuphome.base.BaseFragment
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var mViewModel: HomeActivityViewModel

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
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getRemoveDeviceFlag().observe(this, Observer<Boolean> {
            if(it)bottom_nav_view.selectedItemId=R.id.tab_device
        })
    }

    fun showBottomNavigationBar(isVisible: Boolean) {
        bottom_nav_view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun getPagePosition():Int{
        return viewPager.currentItem
    }
}
