package com.ihomey.linkuphome.device

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.csr.mesh.ConfigModelApi
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.adapter.ScanDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.dialog.DeviceAssociateFragment
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.protocol.csrmesh.CSRMeshServiceManager
import com.ihomey.linkuphome.protocol.sigmesh.MeshDeviceAssociateListener
import com.ihomey.linkuphome.protocol.sigmesh.MeshDeviceScanListener
import com.ihomey.linkuphome.protocol.sigmesh.SigMeshServiceManager
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.pairlink.sigmesh.lib.PlSigMeshService
import kotlinx.android.synthetic.main.connect_device_fragment.*


class ConnectDeviceFragment : BaseFragment(), FragmentBackHandler,  DeviceListAdapter.OnCheckedChangeListener, BaseQuickAdapter.OnItemClickListener, DeviceListAdapter.OnSeekBarChangeListener, MeshDeviceScanListener, MeshDeviceAssociateListener {

    companion object {
        fun newInstance() = ConnectDeviceFragment()
    }

    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var viewModel: ConnectDeviceViewModel
    private lateinit var adapter: ScanDeviceListAdapter
    private val deviceAssociateFragment = DeviceAssociateFragment()

    private var currentZone: Zone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connect_device_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ConnectDeviceViewModel::class.java)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentZone = it.data
                currentZone?.id?.let { viewModel.setQuery(it, arguments?.getInt("deviceType")!!) }
            }
        })
        viewModel.devicesResult.observe(viewLifecycleOwner, Observer<Resource<List<Device>>> {
            if (it?.status == Status.SUCCESS && (adapter.emptyViewCount == 1 && adapter.itemCount == 1)) {
                adapter.setNewData(it.data)
                val scanDevice = mViewModel.getScanDevice().value
                if (scanDevice != null && adapter.data.indexOf(scanDevice) == -1) {
                    adapter.addData(scanDevice)
                }
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let {
            val homeActivity =it as HomeActivity
            homeActivity.resetDevices()
        }
    }

    override fun onBackPressed(): Boolean {
        Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
        return true
    }

    override fun onDeviceFound(device: Device) {
        arguments?.getInt("deviceType")?.let {
            if(it==device.type){
                if (adapter.data.indexOf(device) == -1) adapter.addData(device)
            }
        }
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
    }


    override fun onStart() {
        super.onStart()
        CSRMeshServiceManager.getInstance().setMeshDeviceScanListener(this)
        SigMeshServiceManager.getInstance().setMeshDeviceScanListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.clearScanedDevice()
        CSRMeshServiceManager.getInstance().setMeshDeviceScanListener(null)
        SigMeshServiceManager.getInstance().setMeshDeviceScanListener(null)
    }

    override fun deviceAssociateFailed(messageRes: Int) {
        deviceAssociateFragment.onAssociateProgressChanged(0)
        deviceAssociateFragment.dismiss()
        activity?.toast(messageRes)
    }

    override fun associationProgress(progress: Int) {
        if (progress in 0..99) {
            deviceAssociateFragment.onAssociateProgressChanged(progress)
        }
    }

    override fun deviceAssociated(deviceId: Int, uuidHash: Int, macAddress: String) {
        val type = arguments?.getInt("deviceType")!!
        val deviceType = DeviceType.values()[type]
        if (TextUtils.isEmpty(macAddress)) {
            context?.getDeviceId()?.let { it1 ->
                mViewModel.saveDevice(it1, currentZone?.id!!, type, deviceType.name, 0, null).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                    if (it?.status == Status.SUCCESS && it.data != null) {
                        it.data.hash = "" + uuidHash
                        val position = adapter.data.indexOf(it.data) ?: -1
                        if (position != -1) {
                            adapter.getItem(position)?.id = it.data.id
                            adapter.getItem(position)?.instructId = it.data.instructId
                            adapter.notifyItemChanged(position)
                        }
                        deviceAssociateFragment.dismiss()
                        if (adapter.data.none { TextUtils.equals("0", it.id) }) Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
                    } else if (it?.status == Status.ERROR) {
                        CSRMeshServiceManager.getInstance().resetDevice(deviceId)
                        deviceAssociateFragment.dismiss()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                })
            }
        } else {
            context?.getDeviceId()?.let { it1 ->
                currentZone?.let {it0->
                    val index=SigMeshServiceManager.getInstance().getMeshIndex(it0)
                    mViewModel.saveDevice(it1, it0.id, type, deviceType.name, deviceId, PlSigMeshService.getInstance().getJsonStrMeshNet(index).encodeBase64()).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                        if (it?.status == Status.SUCCESS) {
                            SigMeshServiceManager.getInstance().adding=false
                            val device = Device(0, deviceType.name, macAddress)
                            val position = adapter.data.indexOf(device) ?: -1
                            if (position != -1) {
                                adapter.getItem(position)?.id = macAddress
                                adapter.getItem(position)?.pid = deviceId
                                adapter.notifyItemChanged(position)
                            }
                            deviceAssociateFragment.dismiss()
                            if (adapter.data.none { TextUtils.equals("0", it.id) }) Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
                        } else if (it?.status == Status.ERROR) {
                            SigMeshServiceManager.getInstance().adding=false
                            SigMeshServiceManager.getInstance().resetDevice(deviceId)
                            deviceAssociateFragment.dismiss()
                            it.message?.let { it2 -> activity?.toast(it2) }
                        }
                    })
                }
            }
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let {
            if (TextUtils.equals("0", it.id)) {
                deviceAssociateFragment.isCancelable = false
                deviceAssociateFragment.show(fragmentManager, "DeviceAssociateFragment")
                if(TextUtils.isEmpty(it.macAddress)){
                    CSRMeshServiceManager.getInstance().associateDevice(it,this)
                }else{
                    PlSigMeshService.getInstance().proxyExit()
                    SigMeshServiceManager.getInstance().associateDevice(it,this)
                }
            }
        }
    }

    override fun onCheckedChanged(singleDevice: Device, isChecked: Boolean) {
        LightControllerFactory().createCommonController(singleDevice)?.setOnOff(isChecked)
        changeDeviceState(singleDevice, "on", if (isChecked) "1" else "0")
    }

    override fun onProgressChanged(singleDevice: Device, progress: Int) {
        LightControllerFactory().createCommonController(singleDevice)?.setBrightness(getMaxBrightness(singleDevice)*progress/100)
        changeDeviceState(singleDevice, "brightness", progress.toString())
    }

    private fun getMaxBrightness(device: Device): Int {
        return when (device.type) {
            3,8 -> if (device.pid != 0) 49514 else 240
            6 ,10-> if (device.pid != 0) 49514 else 22
            else -> if (device.pid != 0) 49514 else 85
        }
    }


    private fun changeDeviceState(device: Device, key: String, value: String) {
        updateState(device, key, value)
        context?.getDeviceId()?.let { it1 ->
            mViewModel.changeDeviceState(it1, device.id, key, value).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                if (it?.status == Status.SUCCESS) {

                } else if (it?.status == Status.ERROR) {

                }
            })
        }
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
}
