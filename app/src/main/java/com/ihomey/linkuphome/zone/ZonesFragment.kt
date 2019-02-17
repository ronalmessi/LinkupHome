package com.ihomey.linkuphome.zone

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
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.SubZoneListAdapter
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.SubZone
import com.ihomey.linkuphome.data.vo.Zone
import com.ihomey.linkuphome.listener.DeleteSubZoneListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.zones_fragment.*

class ZonesFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener, DeleteSubZoneListener {

    companion object {
        fun newInstance() = ZonesFragment()
    }

    private lateinit var viewModel: ZonesViewModel
    private lateinit var adapter: SubZoneListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zones_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ZonesViewModel::class.java)
        viewModel.getCurrentZone().observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                tv_title.text = it.data.name
                viewModel.setCurrentZone(it.data)
            }
        })

        viewModel.subZonesResult.observe(this, Observer<Resource<List<SubZone>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
                if (it.data != null && !it.data.isEmpty()) iv_add.visibility = View.VISIBLE else iv_add.visibility = View.INVISIBLE
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SubZoneListAdapter(R.layout.item_sub_zone_list)
        adapter.onItemChildClickListener = this
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_zone_list.addItemDecoration(it) }
        rcv_zone_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_zone_list_empty, rcv_zone_list)
        adapter.emptyView?.findViewById<FloatingActionButton>(R.id.btn_create_zone)?.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_chooseZoneTypeFragment) }
        iv_add.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_chooseZoneTypeFragment) }
    }


    override fun deleteSubZone(id: Int) {
        viewModel.deleteSubZone(id)
    }


    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val subZOne = adapter.getItem(position)
        if (subZOne != null) {
            when (view.id) {
                R.id.btn_delete -> {
                    val dialog = DeleteSubZoneFragment()
                    val bundle = Bundle()
                    bundle.putInt("zoneId", subZOne.id)
                    dialog.arguments = bundle
                    dialog.setDeleteSubZoneListener(this)
                    dialog.show(fragmentManager, "DeleteSubZoneFragment")
                    (view.parent as SwipeLayout).close(true)
                }
            }
        }
    }


}
