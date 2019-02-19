package com.ihomey.linkuphome.device1

import android.content.Context
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
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.UnBondedDeviceListAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.group.GroupUpdateFragment

import com.ihomey.linkuphome.listener.GroupUpdateListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.unbonded_devices_fragment.*

class UnBindedDevicesFragment : Fragment(), BondDeviceTipFragment.BondDeviceListener, GroupUpdateListener {

    companion object {
        fun newInstance() = UnBindedDevicesFragment()
    }

    private lateinit var mViewModel: UnBindedDevicesViewModel
    private lateinit var adapter: UnBondedDeviceListAdapter
    private lateinit var listener: BindDeviceListener
    private val mDialog: GroupUpdateFragment = GroupUpdateFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.unbonded_devices_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BindDeviceListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(UnBindedDevicesViewModel::class.java)
        mViewModel.devicesResult.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                if (adapter.itemCount == 1) adapter.setNewData(it.data)
                if (it.data.isNullOrEmpty()) adapter.clearSelectedDeviceIds()
            }
        })
        arguments?.getInt("subZoneId")?.let { mViewModel.setSubZoneId(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UnBondedDeviceListAdapter(R.layout.item_unbonded_device)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_unbonded_device_list_empty, rcv_device_list)
        iv_back.setOnClickListener {
            if (adapter.selectedDeviceIds.isEmpty()) Navigation.findNavController(it).popBackStack() else {
                val dialog = BondDeviceTipFragment()
                dialog.isCancelable = false
                dialog.setDeleteDeviceListener(this)
                dialog.show(fragmentManager, "BondDeviceTipFragment")
            }
        }
        btn_save.setOnClickListener { if(adapter.selectedDeviceIds.isNullOrEmpty()) confirm() else bindDevice()}
    }

    override fun confirm() {
        Navigation.findNavController(btn_save).popBackStack()
    }

    private fun bindDevice() {
        val bundle = Bundle()
        bundle.putInt("updateType", 0)
        mDialog.arguments = bundle
        mDialog.isCancelable = false
        mDialog.show(fragmentManager, "GroupUpdateFragment")
        arguments?.getInt("subZoneId")?.let {
            for (deviceId in adapter.selectedDeviceIds) {
                listener.bindDevice(deviceId, it, this)
            }
        }
    }

    override fun groupsUpdated(deviceId: Int, groupId: Int, groupIndex: Int, success: Boolean, msg: String?) {
        adapter.selectedDeviceIds.remove(deviceId)
        if (success) {
            mViewModel.createModel(Model1(0, deviceId, groupId, groupIndex,-1))
        } else {
            if (msg != null) activity?.toast(msg)
        }
        if (adapter.selectedDeviceIds.isEmpty()){
            mDialog.dismiss()
            Navigation.findNavController(btn_save).popBackStack()
        }
    }

    interface BindDeviceListener {
        fun bindDevice(deviceId: Int, groupId: Int, groupUpdateListener: GroupUpdateListener)
    }
}
