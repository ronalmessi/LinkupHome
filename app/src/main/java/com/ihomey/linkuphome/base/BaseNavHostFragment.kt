package com.ihomey.linkuphome.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagedList
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.FragmentVisibleStateListener
import com.ihomey.linkuphome.setting.zone.ZoneSettingViewModel


class BaseNavHostFragment : BaseFragment(), FragmentBackHandler {

    private var listener: FragmentVisibleStateListener? = null

    var isVisibleToUser: Boolean = false

    private lateinit var viewModel: ZoneSettingViewModel

    fun newInstance(navGraphId: Int): BaseNavHostFragment {
        val baseNavHostFragment = BaseNavHostFragment()
        val bundle = Bundle()
        bundle.putInt("navGraphId", navGraphId)
        baseNavHostFragment.arguments = bundle
        return baseNavHostFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.base_navhost_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val navGraphId = arguments?.getInt("navGraphId")
            navGraphId?.let {
                val finalHost = NavHostFragment.create(it)
                childFragmentManager.beginTransaction().replace(R.id.nav_host, finalHost).setPrimaryNavigationFragment(finalHost).commit()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.getInt("navGraphId")?.let {
            if (it == R.navigation.nav_setting) {
                viewModel = ViewModelProviders.of(this).get(ZoneSettingViewModel::class.java)
                viewModel.zoneResult.observe(viewLifecycleOwner, Observer<PagedList<Zone>> {})
            }
        }
    }

    fun showBottomNavigationBar(isVisible: Boolean) {
        if (parentFragment != null && parentFragment is HomeFragment) {
            (parentFragment as HomeFragment).showBottomNavigationBar(isVisible)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
        if (listener != null) listener?.onFragmentVisibleStateChanged(isVisibleToUser)
        if(isVisibleToUser){
            arguments?.getInt("navGraphId")?.let {
                if (it == R.navigation.nav_setting) {
                    viewModel.loadLocalZones()
                }
            }
        }
    }


    override fun onBackPressed(): Boolean {
        return handleBackPress(childFragmentManager.fragments[1])
    }

    fun setFragmentVisibleStateListener(listener: FragmentVisibleStateListener) {
        this.listener = listener
    }

}
