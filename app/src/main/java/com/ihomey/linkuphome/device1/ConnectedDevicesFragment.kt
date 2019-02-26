package com.ihomey.linkuphome.device1

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
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
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device.DeviceRemoveFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.BottomNavigationVisibilityListener
import com.ihomey.linkuphome.listener.DeleteDeviceListener
import com.ihomey.linkuphome.listeners.DeviceRemoveListener
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.connected_devices_fragment.*


open class ConnectedDevicesFragment : BaseFragment(), BaseQuickAdapter.OnItemChildClickListener, DeviceRemoveListener, DeleteDeviceListener, BaseQuickAdapter.OnItemClickListener, DeviceListAdapter.OnCheckedChangeListener, DeviceListAdapter.OnSeekBarChangeListener {

    companion object {
        fun newInstance() = ConnectedDevicesFragment()
    }

    private lateinit var viewModel: HomeActivityViewModel
    protected lateinit var mViewModel: ConnectedDevicesViewModel
    private lateinit var adapter: DeviceListAdapter
    private lateinit var listener: DevicesFragment.DevicesStateListener
    private lateinit var bottomNavigationVisibilityListener: BottomNavigationVisibilityListener
    private lateinit var meshServiceStateListener: MeshServiceStateListener
    private val deviceRemoveFragment = DeviceRemoveFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connected_devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ConnectedDevicesViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.devicesResult.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
            }
        })
        arguments?.getInt("zoneId")?.let { mViewModel.setCurrentZoneId(it) }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as DevicesFragment.DevicesStateListener
        bottomNavigationVisibilityListener = context as BottomNavigationVisibilityListener
        meshServiceStateListener = context as MeshServiceStateListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DeviceListAdapter(R.layout.item_device_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener = this
        adapter.setOnCheckedChangeListener(this)
        adapter.setOnSeekBarChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
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
//                    mViewModel.setCurrentControlDevice(singleDevice)
//                    when (singleDevice.type) {
//                        4 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_c3ControlFragment)
//                        3 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_r2ControlFragment)
//                    }
                }
            }
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
//        val singleDevice = adapter.getItem(position)
//        if (singleDevice != null) {
//            mViewModel.setCurrentControlDevice(singleDevice)
//            when (singleDevice.type) {
//                4 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_r2ControlFragment)
//                3 -> Navigation.findNavController(view).navigate(R.id.action_tab_devices_to_r2ControlFragment)
//            }
//        }
    }

    override fun onCheckedChanged(item: SingleDevice, isChecked: Boolean) {
//        val controller = ControllerFactory().createController(item.type)
//        item.state.on = if (isChecked) 1 else 0
//        if (meshServiceStateListener.isMeshServiceConnected()) {
//            controller?.setLightPowerState(item.id, if (isChecked) 1 else 0)
//        }
//        mViewModel.updateDevice(item)
    }

    override fun onProgressChanged(item: SingleDevice, progress: Int) {
//        val controller = ControllerFactory().createController(item.type)
//        if (meshServiceStateListener.isMeshServiceConnected()) controller?.setLightBright(item.id, progress.plus(15))
//        item.state.brightness = progress
//        mViewModel.updateDevice(item)
    }

    override fun onDeviceRemoved(deviceId: Int, uuidHash: Int, success: Boolean) {
        val device = SingleDevice(deviceId, 0, "", 0, uuidHash, 0, 0, 0)
        val position = adapter.data.indexOf(device) ?: -1
        if (position != -1) {
            adapter.remove(position)
            viewModel.deleteSingleDevice(deviceId)
        }
        deviceRemoveFragment.dismiss()
    }
}
