package com.ihomey.linkuphome.device1

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.devices_fragment.*

class DevicesFragment : BaseFragment() {

    companion object {
        fun newInstance() = DevicesFragment()
    }

    private lateinit var viewModel: DevicesViewModel
    private var adapter: DeviceListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DeviceListAdapter(R.layout.lamp_device_mesh_list_item)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        rcv_device_list.adapter = adapter
        adapter?.setEmptyView(R.layout.view_device_list_empty,rcv_device_list)
        adapter?.emptyView?.findViewById<Button>(R.id.btn_add_device)?.setOnClickListener {  Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_chooseDeviceTypeFragment) }
    }
}
