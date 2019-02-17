package com.ihomey.linkuphome.device1

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.daimajia.swipe.SwipeLayout
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device.DeviceRemoveFragment
import com.ihomey.linkuphome.listener.DeleteDeviceListener
import com.ihomey.linkuphome.listeners.DeviceRemoveListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.devices_fragment.*

class DevicesFragment : BaseFragment(), BaseQuickAdapter.OnItemChildClickListener, DeviceRemoveListener, DeleteDeviceListener, BaseQuickAdapter.OnItemClickListener {


    companion object {
        fun newInstance() = DevicesFragment()
    }


    private lateinit var viewModel: DevicesViewModel
    private lateinit var adapter: DeviceListAdapter
    private lateinit var listener: DevicesStateListener
    private val deviceRemoveFragment = DeviceRemoveFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        viewModel.getDevices().observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
                if (it.data != null && !it.data.isEmpty()) iv_add.visibility = View.VISIBLE else iv_add.visibility = View.INVISIBLE
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as DevicesStateListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DeviceListAdapter(R.layout.item_device_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener=this
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_device_list_empty, rcv_device_list)
        adapter.emptyView?.findViewById<Button>(R.id.btn_add_device)?.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_chooseDeviceTypeFragment) }
        iv_add.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_chooseDeviceTypeFragment) }
    }


    override fun deleteDevice(id: Int, hash: Int) {
        deviceRemoveFragment.isCancelable = false
        deviceRemoveFragment.show(fragmentManager, "DeviceRemoveFragment")
        listener.removeDevice(id, hash, this)
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val singleDevice = adapter.getItem(position)
        if (singleDevice != null) {
            when (view.id) {
                R.id.btn_delete -> {
                    val dialog = DeleteDeviceFragment()
                    val bundle = Bundle()
                    bundle.putInt("deviceId", singleDevice.id)
                    bundle.putInt("deviceHash", singleDevice.hash)
                    dialog.arguments = bundle
                    dialog.setDeleteDeviceListener(this)
                    dialog.show(fragmentManager, "DeleteDeviceFragment")
                    (view.parent as SwipeLayout).close(true)
                }
                R.id.tv_device_name->{ Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_c3ControlFragment)}
            }
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val singleDevice = adapter.getItem(position)
        if (singleDevice != null) {
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_c3ControlFragment)
        }
    }

    override fun onDeviceRemoved(deviceId: Int, uuidHash: Int, success: Boolean) {
        deviceRemoveFragment.dismiss()
        viewModel.deleteSingleDevice(deviceId)
    }


    interface DevicesStateListener {
        fun removeDevice(deviceId: Int, deviceHash: Int, listener: DeviceRemoveListener)
    }
}
