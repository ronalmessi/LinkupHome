package com.ihomey.linkuphome.share1

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ShareZoneListAdapter
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.Zone
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeFragment

import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.ihomey.linkuphome.zone.ReNameZoneFragment
import kotlinx.android.synthetic.main.zone_share_list_fragment.*

class JoinZoneFragment : BaseFragment() {

    companion object {
        fun newInstance() = JoinZoneFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_join_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

}
