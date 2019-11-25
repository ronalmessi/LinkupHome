package com.ihomey.linkuphome.devicecontrol.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.viewholder.WarmColdControlViewHolder
import com.ihomey.linkuphome.home.HomeActivityViewModel

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class WarmColdControlView : BaseFragment() {

    private lateinit var mViewModel: HomeActivityViewModel

    private lateinit var contentView: View


    fun newInstance(): WarmColdControlView {
        return WarmColdControlView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.control_warm_cold_fragment, container, false)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(HomeActivityViewModel::class.java)
            mViewModel.getCurrentControlDevice().observe(viewLifecycleOwner, Observer<Device> {
                WarmColdControlViewHolder(contentView).bindTo(it)
            })
        }
    }
}