package com.ihomey.linkuphome.setting.zone.share

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ShareZoneListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.LoadingFragment
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.setting.zone.ZoneSettingViewModel
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.zone_share_list_fragment.*

class ShareZoneListFragment : BaseFragment(), ShareZoneListAdapter.OnItemChildClickListener {


    companion object {
        fun newInstance() = ShareZoneListFragment()
    }

    private lateinit var mViewModel: ZoneSettingViewModel
    private lateinit var adapter: ShareZoneListAdapter
    private lateinit var loadingFragment: LoadingFragment


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_share_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentFragment?.parentFragment?.let {
            mViewModel = ViewModelProviders.of(it).get(ZoneSettingViewModel::class.java)
            mViewModel.zoneResult.observe(viewLifecycleOwner, Observer<PagedList<Zone>> {
                adapter.submitList(it)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingFragment = LoadingFragment()
        adapter = ShareZoneListAdapter()
        adapter.setOnItemChildClickListener(this)
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(LinearLayoutManager.HORIZONTAL, context?.resources?.getDimension(R.dimen._63sdp)?.toInt()!!, it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_zone_list.addItemDecoration(it) }
        rcv_zone_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

    override fun onItemChildClick(position: Int, view: View) {
        adapter.currentList?.get(position)?.let {
            context?.getIMEI()?.let { it1 ->
                mViewModel.shareZone(it1, it.id).observe(viewLifecycleOwner, Observer<Resource<String>> {
                    if (it?.status == Status.SUCCESS) {
                        hideLoadingView()
                        if (!TextUtils.isEmpty(it.data)) {
                            val bundle = Bundle()
                            bundle.putString("invitationCode", it.data)
                            Navigation.findNavController(view).navigate(R.id.action_shareZoneListFragment_to_shareZoneFragment, bundle)
                        }
                    } else if (it?.status == Status.ERROR) {
                        hideLoadingView()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    } else if (it?.status == Status.LOADING) {
                        showLoadingView()
                    }
                })
            }
        }
    }
}
