package com.ihomey.linkuphome.zone

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ZoneTypeListAdapter
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.Zone
import com.ihomey.linkuphome.listener.CreateSubZoneListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.choose_zone_type_fragment.*

class ChooseZoneTypeFragment : Fragment(), BaseQuickAdapter.OnItemClickListener, CreateSubZoneListener {

    companion object {
        fun newInstance() = ChooseZoneTypeFragment()
    }

    private lateinit var adapter: ZoneTypeListAdapter
    private lateinit var viewModel: ChooseZoneTypeViewModel

    private var zone: Zone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.choose_zone_type_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChooseZoneTypeViewModel::class.java)
        viewModel.getZones().observe(this, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS) {
                zone = it.data?.find { it.isCurrent }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ZoneTypeListAdapter(R.layout.item_zone_type_list)
        adapter.onItemClickListener = this
        rcv_zone_type_list.layoutManager = GridLayoutManager(context, 3)
        context?.resources?.getDimensionPixelSize(R.dimen._24sdp)?.let { SpaceItemDecoration(it, it, it, it) }?.let { rcv_zone_type_list.addItemDecoration(it) }
        rcv_zone_type_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack() }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val dialog = CreateSubZoneFragment()
        val bundle = Bundle()
        adapter.getItem(position)?.let { bundle.putInt("zoneTYpe", it) }
        dialog.arguments = bundle
        dialog.setCreateSubZoneListener(this)
        dialog.show(fragmentManager, "CreateSubZoneFragment")
    }


    override fun createSubZone(type: Int, name: String) {
        if (zone != null) zone?.id?.let {
            viewModel.createSubZone(type, name, it)
            Navigation.findNavController(activity!!, R.id.nav_host).popBackStack()
        }
    }


}
