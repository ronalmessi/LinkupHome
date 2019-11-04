package com.ihomey.linkuphome.zone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.FragmentVisibleStateListener
import com.ihomey.linkuphome.toast


class ZoneNavHostFragment : BaseFragment(), FragmentBackHandler {


    private var listener: FragmentVisibleStateListener? = null
    private lateinit var viewModel: ZoneSettingViewModel

    var isVisibleToUser: Boolean = false

    fun newInstance(): ZoneNavHostFragment {
        return ZoneNavHostFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.zone_navhost_fragment, container, false)
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ZoneSettingViewModel::class.java)
        context?.getIMEI()?.let { it1 ->
            viewModel.getRemoteZones(it1).observe(viewLifecycleOwner, Observer<Resource<List<Zone>>> {
                if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }


    override fun onBackPressed(): Boolean {
        return handleBackPress(childFragmentManager.fragments[0])
    }

    fun setFragmentVisibleStateListener(listener: FragmentVisibleStateListener) {
        this.listener = listener
    }

}
