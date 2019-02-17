package com.ihomey.linkuphome.zone

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ZoneListAdapter
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.Zone
import com.ihomey.linkuphome.listener.UpdateZoneNameListener
import com.ihomey.linkuphome.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.zone_setting_fragment.*

class ZoneSettingFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener, UpdateZoneNameListener, BaseQuickAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ZoneSettingFragment()
    }

    private lateinit var viewModel: ZoneSettingViewModel
    private lateinit var adapter: ZoneListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ZoneSettingViewModel::class.java)
        viewModel.getZones().observe(this, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ZoneListAdapter(R.layout.item_zone_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener = this
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(context, LinearLayoutManager.VERTICAL, it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_zone_list.addItemDecoration(it) }
        rcv_zone_list.adapter = adapter

        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack() }

        btn_create_zone.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isCurrent", false)
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_zoneSettingFragment_to_createZoneFragment, bundle)
        }
        btn_join_zone.setOnClickListener {
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_zoneSettingFragment_to_createZoneFragment)
        }
        btn_share_zone.setOnClickListener {
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_zoneSettingFragment_to_createZoneFragment)
        }
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val zone = adapter.getItem(position)
        if (zone != null) {
            val dialog = ReNameZoneFragment()
            val bundle = Bundle()
            bundle.putInt("zoneId", zone.id)
            bundle.putString("zoneName", zone.name)
            dialog.arguments = bundle
            dialog.setUpdateZoneNameListener(this)
            dialog.show(fragmentManager, "ReNameZoneFragment")
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val zone = adapter.getItem(position)
        if (zone != null) {
            viewModel.setCurrentZone(zone.id)
            Navigation.findNavController(activity!!, R.id.nav_host).popBackStack()
        }
    }

    override fun updateZoneName(id: Int, newName: String) {
        viewModel.updateZoneName(newName, id)
    }

}
