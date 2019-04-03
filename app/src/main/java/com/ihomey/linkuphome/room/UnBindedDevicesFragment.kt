package com.ihomey.linkuphome.room

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.UnBondedDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.group.GroupUpdateFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.GroupUpdateListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.unbonded_devices_fragment.*

class UnBindedDevicesFragment : BaseFragment(), BondDeviceTipFragment.BondDeviceListener, GroupUpdateListener {

    companion object {
        fun newInstance() = UnBindedDevicesFragment()
    }

    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var mViewModel: RoomViewModel
    private lateinit var adapter: UnBondedDeviceListAdapter
    private lateinit var listener: BindDeviceListener
    private val mDialog: GroupUpdateFragment = GroupUpdateFragment()

    private lateinit var room: Room

    private var count: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.unbonded_devices_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BindDeviceListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(parentFragment!!).get(RoomViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.mSelectedRoom.observe(this, Observer<Room> {
            room = it
        })
        viewModel.devicesResult.observe(viewLifecycleOwner, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data?.filter { (it.roomId != room.id && it.roomId == 0) })
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UnBondedDeviceListAdapter(R.layout.item_unbonded_device)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_unbonded_device_list_empty, rcv_device_list)
        iv_back.setOnClickListener {
            if (adapter.getSelectedDevices().isEmpty()) Navigation.findNavController(it).popBackStack() else {
                val dialog = BondDeviceTipFragment()
                dialog.isCancelable = false
                dialog.setDeleteDeviceListener(this)
                dialog.show(fragmentManager, "BondDeviceTipFragment")
            }
        }
        btn_save.setOnClickListener {
            if (adapter.getSelectedDevices().isEmpty()) {
                confirm()
            } else {
                bindDevice()
            }
        }
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
        for (device in adapter.getSelectedDevices()) {
            listener.bindDevice(device.instructId, room.instructId, "and", this)
        }
    }

    override fun groupsUpdated(deviceId: Int, groupId: Int, action: String, success: Boolean, msg: String?) {
        if (success) {
            bindDevice(room.zoneId, groupId, deviceId, "add")
        } else {
            msg?.let { activity?.toast(it) }
        }
    }

    interface BindDeviceListener {
        fun bindDevice(deviceInstructId: Int, groupInstructId: Int, action: String, groupUpdateListener: GroupUpdateListener?)
    }


    private fun bindDevice(zoneId: Int, groupInstructId: Int, deviceInstructId: Int, act: String) {
        context?.getIMEI()?.let { it1 ->
            viewModel.bindDevice(it1, zoneId, groupInstructId, deviceInstructId, act).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                if (it?.status == Status.SUCCESS) {
                    count++
                    if (count == adapter.getSelectedDevices().size) {
                        mDialog.dismiss()
                        count = 0
                        Navigation.findNavController(btn_save).popBackStack()
                    }
                } else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }
}
