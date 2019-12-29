package com.ihomey.linkuphome.device

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.DeviceType
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.adapter.ScanDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.devicecontrol.controller.impl.M1Controller
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.protocol.sigmesh.BleDeviceScanListener
import com.ihomey.linkuphome.protocol.csrmesh.CSRMeshServiceManager
import com.ihomey.linkuphome.protocol.spp.BluetoothSPP
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.connect_device_fragment.*


class ConnectM1DeviceFragment : BaseFragment(), FragmentBackHandler, DeviceListAdapter.OnCheckedChangeListener, BaseQuickAdapter.OnItemClickListener, DeviceListAdapter.OnSeekBarChangeListener, BleDeviceScanListener {

    companion object {
        fun newInstance() = ConnectM1DeviceFragment()
    }

    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var viewModel: ConnectDeviceViewModel
    private lateinit var adapter: ScanDeviceListAdapter
    private var connectingDeviceAddress: String? = null

    private var currentZone: Zone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connect_device_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ConnectDeviceViewModel::class.java)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(viewLifecycleOwner, Observer<Resource<Zone>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentZone = it.data
                currentZone?.id?.let { viewModel.setQuery(it, 0) }
            }
        })
        viewModel.devicesResult.observe(viewLifecycleOwner, Observer<Resource<List<Device>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data?.onEach { it.macAddress = it.id })
                CSRMeshServiceManager.getInstance().setBleDeviceScanListener(this)
                CSRMeshServiceManager.getInstance().startScan()
            }
        })
    }

    override fun onBackPressed(): Boolean {
        Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ScanDeviceListAdapter(arrayListOf())
        adapter.onItemClickListener = this
        adapter.setOnSeekBarChangeListener(this)
        adapter.setOnCheckedChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_scan_device_list_empty, rcv_device_list)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack(R.id.tab_devices, false) }
        BluetoothSPP.getInstance()?.setBluetoothConnectionListener(mBluetoothConnectionListener)
    }


    override fun onDeviceFound(device: Device) {
        if (adapter.data.indexOf(device) == -1&&device.type==0) adapter.addData(device)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setNewData(null)
        BluetoothSPP.getInstance()?.setBluetoothConnectionListener(null)
        CSRMeshServiceManager.getInstance().setBleDeviceScanListener(null)
        CSRMeshServiceManager.getInstance().stopScan()
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let { it0 ->
            if (TextUtils.equals("0", it0.id)) {
                val pairedDeviceAddress = BluetoothSPP.getInstance()?.pairedDeviceAddress
                if (pairedDeviceAddress != null && pairedDeviceAddress.any { TextUtils.equals(it, it0.macAddress) }) {
                    connectingDeviceAddress = it0.macAddress
                    showLoadingView()
                    BluetoothSPP.getInstance()?.connect(it0.macAddress)
                } else {
                    Navigation.findNavController(iv_back).navigate(R.id.action_connectM1DeviceFragment_to_m1InstructionFragment)
                }
            }
        }
    }


    override fun onCheckedChanged(singleDevice: Device, isChecked: Boolean) {
        LightControllerFactory().createCommonController(singleDevice)?.setOnOff(isChecked)
        updateState(singleDevice, "on", if (isChecked) "1" else "0")
    }

    override fun onProgressChanged(singleDevice: Device, progress: Int) {
        LightControllerFactory().createCommonController(singleDevice)?.setBrightness(progress)
        updateState(singleDevice, "brightness", progress.toString())
    }

    private fun updateState(device: Device, key: String, value: String) {
        if (TextUtils.equals("brightness", key)) {
            val deviceState = device.parameters
            deviceState?.let {
                it.brightness = value.toInt()
                mViewModel.updateDeviceState(device, it)
            }
        } else {
            val deviceState = device.parameters
            deviceState?.let {
                it.on = value.toInt()
                mViewModel.updateRoomAndDeviceState(device, it)
            }
        }
    }

    private val mBluetoothConnectionListener = object : BluetoothSPP.BluetoothConnectionListener {
        override fun onDeviceConnecting(name: String?, address: String?) {

        }

        override fun onDeviceConnected(name: String?, address: String) {
            connectingDeviceAddress?.let {
                if (TextUtils.equals(address, it)) {
                    val device = Device(0, DeviceType.values()[0].name, address)
                    val position = adapter.data.indexOf(device) ?: -1
                    if (position != -1) {
                        adapter.getItem(position)?.id = address
                        adapter.notifyItemChanged(position)
                    }
                    viewModel.saveDevice(0, currentZone?.id!!, DeviceType.values()[0].name, address)
                    val controller = M1Controller()
                    controller.getFirmwareVersion(address)
                    if (adapter.data.none { TextUtils.equals("0", it.id) }&&isVisible) {
                        hideLoadingView()
                        val bundle =Bundle()
                        bundle.putBoolean("navigateFromConnectM1Device",true)
                        Navigation.findNavController(iv_back).navigate(R.id.action_connectM1DeviceFragment_to_devicesFragment,bundle)
                    }
                }
            }
        }

        override fun onDeviceConnectFailed(name: String?, address: String) {
            connectingDeviceAddress?.let {
                if (TextUtils.equals(address, it)) {
                    if (isVisible) {
                        hideLoadingView()
                        activity?.toast(R.string.msg_m1_connect_failed, Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }
}
