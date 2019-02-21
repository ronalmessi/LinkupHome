package com.ihomey.linkuphome.zone

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.BindedDeviceListAdapter
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device1.ReNameDeviceFragment
import com.ihomey.linkuphome.device1.UnBindedDevicesFragment
import com.ihomey.linkuphome.group.GroupUpdateFragment
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.GroupUpdateListener
import com.ihomey.linkuphome.listener.UpdateDeviceNameListener
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import kotlinx.android.synthetic.main.sub_zone_fragment.*


class SubZoneFragment : Fragment(), BindedDeviceListAdapter.OnCheckedChangeListener, SwipeMenuItemClickListener, BaseQuickAdapter.OnItemClickListener, GroupUpdateListener, UpdateDeviceNameListener {


    companion object {
        fun newInstance() = SubZoneFragment()
    }

    private lateinit var mViewModel: SubZoneViewModel
    private lateinit var adapter: BindedDeviceListAdapter
    private lateinit var listener: MeshServiceStateListener
    private lateinit var bindDeviceListener: UnBindedDevicesFragment.BindDeviceListener

    private val mDialog: GroupUpdateFragment = GroupUpdateFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sub_zone_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(SubZoneViewModel::class.java)
        mViewModel.devicesResult.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
                if (it.data.isNullOrEmpty()) btn_add_device.visibility = View.GONE else btn_add_device.visibility = View.VISIBLE
            }
        })
        arguments?.getInt("zoneId")?.let { mViewModel.setSubZoneId(it) }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
        bindDeviceListener = context as UnBindedDevicesFragment.BindDeviceListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(false)
        tv_title.text = arguments?.getString("zoneName")
        adapter = BindedDeviceListAdapter(R.layout.item_binded_device)
        adapter.onItemClickListener = this
        adapter.setOnCheckedChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context?.resources?.getDimension(R.dimen._72sdp)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width!!.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }
        rcv_device_list.setSwipeMenuCreator(swipeMenuCreator)
        rcv_device_list.setSwipeMenuItemClickListener(this)
        rcv_device_list.adapter = adapter

        adapter.setEmptyView(R.layout.view_device_list_empty, rcv_device_list)
        adapter.emptyView?.findViewById<Button>(R.id.btn_add_device)?.setOnClickListener {
            val bundle = Bundle()
            arguments?.getInt("zoneId")?.let { it1 -> bundle.putInt("subZoneId", it1) }
            Navigation.findNavController(it).navigate(R.id.action_subZoneFragment_to_unBondedDevicesFragment2, bundle)

        }
        btn_add_device.setOnClickListener {
            val bundle = Bundle()
            arguments?.getInt("zoneId")?.let { it1 -> bundle.putInt("subZoneId", it1) }
            Navigation.findNavController(it).navigate(R.id.action_subZoneFragment_to_unBondedDevicesFragment2, bundle)

        }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        tv_title.setOnClickListener {
            val dialog = ReNameDeviceFragment()
            val bundle = Bundle()
            arguments?.getInt("zoneId")?.let { it1 -> bundle.putInt("deviceId", it1) }
            bundle.putString("deviceName", arguments?.getString("zoneName"))
            dialog.arguments = bundle
            dialog.setUpdateZoneNameListener(this)
            dialog.show(fragmentManager, "ReNameDeviceFragment")
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, position: Int) {
        val singleDevice = adapter.getItem(position)
        if (singleDevice != null) {
            arguments?.getInt("zoneId")?.let {
                val bundle = Bundle()
                bundle.putInt("updateType", 1)
                mDialog.arguments = bundle
                mDialog.isCancelable = false
                mDialog.show(fragmentManager, "GroupUpdateFragment")
                bindDeviceListener.bindDevice(singleDevice.id, it, this)
            }
        }
        menuBridge?.closeMenu()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    override fun updateDeviceName(id: Int, newName: String) {
        mViewModel.updateSubZoneName(newName, id)
        tv_title.text = newName
    }

    override fun onCheckedChanged(item: SingleDevice, isChecked: Boolean) {
        val controller = ControllerFactory().createController(item.device.type)
        item.state?.on = if (isChecked) 1 else 0
        if (listener.isMeshServiceConnected()) controller?.setLightPowerState(item.id, if (isChecked) 1 else 0)
        mViewModel.updateDevice(item)
    }

    override fun groupsUpdated(deviceId: Int, groupId: Int, groupIndex: Int, success: Boolean, msg: String?) {
        mDialog.dismiss()
        if (success) {
            arguments?.getInt("zoneId")?.let { mViewModel.deleteModel(deviceId, it, groupIndex) }
        } else {
            if (msg != null) activity?.toast(msg)
        }
    }

}