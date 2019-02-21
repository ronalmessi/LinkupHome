package com.ihomey.linkuphome.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
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
        bottom_nav_view.setOnNavigationItemSelectedListener { item ->
            onNavDestinationSelected(item, Navigation.findNavController(activity!!, R.id.home_nav_host_fragment))
        }

        Log.d("ic_setting_zone", "1111")
//        bottom_nav_view.selectedItemId=R.id.tab_zones
//        onNavDestinationSelected(bottom_nav_view.menu.getItem(1), Navigation.findNavController(activity!!, R.id.home_nav_host_fragment))
    }


    fun showBottomNavigationBar(isVisible: Boolean) {
        bottom_nav_view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}
