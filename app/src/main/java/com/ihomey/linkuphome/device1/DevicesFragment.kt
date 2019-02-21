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
import com.ihomey.linkuphome.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device.DeviceRemoveFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.DeleteDeviceListener
import com.ihomey.linkuphome.listeners.DeviceRemoveListener
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.devices_fragment.*

open class DevicesFragment : BaseFragment(), BaseQuickAdapter.OnItemChildClickListener, DeviceRemoveListener, DeleteDeviceListener, BaseQuickAdapter.OnItemClickListener, DeviceListAdapter.OnCheckedChangeListener, DeviceListAdapter.OnSeekBarChangeListener {

    companion object {
        fun newInstance() = DevicesFragment()
    }

    private lateinit var viewModel: DevicesViewModel
    protected lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: DeviceListAdapter
    private lateinit var listener: DevicesStateListener
    private lateinit var meshServiceStateListener: MeshServiceStateListener
    private val deviceRemoveFragment = DeviceRemoveFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        mViewModel.devicesResult.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
                if (it.data != null && !it.data.isEmpty()) iv_add.visibility = View.VISIBLE else iv_add.visibility = View.INVISIBLE
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as DevicesStateListener
        meshServiceStateListener = context as MeshServiceStateListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(true)
        adapter = DeviceListAdapter(R.layout.item_device_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener = this
        adapter.setOnCheckedChangeListener(this)
        adapter.setOnSeekBarChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_device_list_empty, rcv_device_list)
        adapter.emptyView?.findViewById<Button>(R.id.btn_add_device)?.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment) }
        iv_add.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment) }
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
                R.id.tv_device_name -> {
                    mViewModel.setCurrentControlDevice(singleDevice)
                    when (singleDevice.type) {
                        4 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_c3ControlFragment)
                        3 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_r2ControlFragment)
                    }
                }
            }
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val singleDevice = adapter.getItem(position)
        if (singleDevice != null) {
            mViewModel.setCurrentControlDevice(singleDevice)
            when (singleDevice.type) {
                4 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_r2ControlFragment)
                3 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_r2ControlFragment)
            }
        }
    }

    override fun onCheckedChanged(item: SingleDevice, isChecked: Boolean) {
        val controller = ControllerFactory().createController(item.type)
        item.state.on = if (isChecked) 1 else 0
        if (meshServiceStateListener.isMeshServiceConnected()) controller?.setLightPowerState(item.id, if (isChecked) 1 else 0)
        mViewModel.updateDevice(item)
    }

    override fun onProgressChanged(item: SingleDevice, progress: Int) {
        val controller = ControllerFactory().createController(item.type)
        if (meshServiceStateListener.isMeshServiceConnected()) controller?.setLightBright(item.id, progress.plus(15))
        item.state.brightness = progress
        mViewModel.updateDevice(item)
    }


    override fun onDeviceRemoved(deviceId: Int, uuidHash: Int, success: Boolean) {
        deviceRemoveFragment.dismiss()
        mViewModel.deleteSingleDevice(deviceId)
    }


    interface DevicesStateListener {
        fun removeDevice(deviceId: Int, deviceHash: Int, listener: DeviceRemoveListener)
    }
}
