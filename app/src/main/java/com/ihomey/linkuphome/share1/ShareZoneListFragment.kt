package com.ihomey.linkuphome.share1

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ShareZoneListAdapter
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.ihomey.linkuphome.zone.ZoneSettingViewModel
import kotlinx.android.synthetic.main.zone_share_list_fragment.*

class ShareZoneListFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener {

    companion object {
        fun newInstance() = ShareZoneListFragment()
    }

    private lateinit var mViewModel: ShareZoneListViewModel
    private lateinit var adapter: ShareZoneListAdapter
    private lateinit var loadingFragment: LoadingFragment


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_share_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ShareZoneListViewModel::class.java)
        mViewModel.getLocalZones().observe(viewLifecycleOwner, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingFragment = LoadingFragment()
        adapter = ShareZoneListAdapter(R.layout.item_zone_share_list)
        adapter.onItemChildClickListener = this
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(context, LinearLayoutManager.VERTICAL, it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_zone_list.addItemDecoration(it) }
        rcv_zone_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val zone=adapter.getItem(position)
        if(zone!=null){
            context?.getIMEI()?.let { it1 ->  mViewModel.shareZone(it1,zone.id).observe(viewLifecycleOwner, Observer<Resource<String>> {
                if (it?.status == Status.SUCCESS) {
                    if(!TextUtils.isEmpty(it.data)){
                        val bundle=Bundle()
                        bundle.putString("invitationCode",it.data)
                        Navigation.findNavController(view).navigate(R.id.action_shareZoneListFragment_to_shareZoneFragment,bundle)
                    }
                }else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2)}
                }
            })}
        }
    }

}
