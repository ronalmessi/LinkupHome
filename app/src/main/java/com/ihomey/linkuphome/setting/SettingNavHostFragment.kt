package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.zone.ZoneSettingViewModel


class SettingNavHostFragment : BaseNavHostFragment() {

        private lateinit var viewModel: ZoneSettingViewModel

    fun newInstance(): SettingNavHostFragment {
        return SettingNavHostFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.navhost_setting_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ZoneSettingViewModel::class.java)
        viewModel.zoneResult.observe(viewLifecycleOwner, Observer<PagedList<Zone>> {})
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){ viewModel.loadLocalZones()}
    }

}
