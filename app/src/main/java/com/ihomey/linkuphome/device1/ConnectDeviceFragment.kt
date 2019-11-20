package com.ihomey.linkuphome.device1

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
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.adapter.ScanDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.LightControllerFactory
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.DeviceAssociateFragment
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeviceAssociateListener
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.pairlink.sigmesh.lib.PlSigMeshService
import kotlinx.android.synthetic.main.connect_device_fragment.*


class ConnectDeviceFragment : BaseFragment(), FragmentBackHandler, DeviceAssociateListener, DeviceListAdapter.OnCheckedChangeListener, BaseQuickAdapter.OnItemClickListener, DeviceListAdapter.OnSeekBarChangeListener {

    companion object {
        fun newInstance() = ConnectDeviceFragment()
    }

    private lateinit var listener: DevicesStateListener
    private lateinit var meshServiceStateListener: MeshServiceStateListener
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

    override fun onBackPressed(): Boolean {
        Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
        return true
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        meshServiceStateListener = context as MeshServiceStateListener
        listener = context as DevicesStateListener
    }

    override fun onResume() {
        super.onResume()
        listener.discoverDevices(true, this)
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

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.clearScanedDevice()
        listener.discoverDevices(false, null)
    }

    override fun onDeviceFound(uuidHash: String, macAddress: String, name: String) {
        val type = arguments?.getInt("deviceType")!!
        val deviceType = DeviceType.values()[type]
        val deviceShortName = getShortName(deviceType)
        if (type == 6 && TextUtils.equals("LinkupHome V1", name)) {
            val singleDevice1 = Device(type, name)
            singleDevice1.macAddress = macAddress
            singleDevice1.hash = uuidHash
            if (adapter.data.indexOf(singleDevice1) == -1) adapter.addData(singleDevice1)
        } else if (type != 6 && TextUtils.equals(deviceShortName, name)) {
            val singleDevice1 = Device(type, deviceType.name)
            singleDevice1.hash = uuidHash
            if (adapter.data.indexOf(singleDevice1) == -1) adapter.addData(singleDevice1)
        }
    }

    override fun deviceAssociated(deviceId: Int, message: String) {
        deviceAssociateFragment.onAssociateProgressChanged(0)
        deviceAssociateFragment.dismiss()
        activity?.toast(message)
    }

    override fun deviceAssociated(deviceId: Int, uuidHash: Int, macAddress: String) {
        val type = arguments?.getInt("deviceType")!!
        val deviceType = DeviceType.values()[type]
        if (TextUtils.isEmpty(macAddress)) {
            context?.getIMEI()?.let { it1 ->
                viewModel.saveDevice(it1, currentZone?.id!!, type, deviceType.name, null, null).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                    if (it?.status == Status.SUCCESS && it.data != null) {
                        it.data.hash = "" + uuidHash
                        val position = adapter.data.indexOf(it.data) ?: -1
                        if (position != -1) {
                            adapter.getItem(position)?.id = it.data.id
                            adapter.getItem(position)?.instructId = it.data.instructId
                            adapter.notifyItemChanged(position)
                        }
                        mViewModel.setCurrentZoneId(currentZone?.id!!)
                        deviceAssociateFragment.dismiss()
                        if (adapter.data.none { TextUtils.equals("0", it.id) }) Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
                    } else if (it?.status == Status.ERROR) {
                        deviceAssociateFragment.dismiss()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                })
            }
        } else {
            context?.getIMEI()?.let { it1 ->
                viewModel.saveDevice(it1, currentZone?.id!!, type, deviceType.name, deviceId, PlSigMeshService.getInstance().getJsonStrMeshNet(0).encodeBase64()).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                    if (it?.status == Status.SUCCESS && it.data != null) {
                        val device = Device(0, "LinkupHome V1", macAddress)
                        val position = adapter.data.indexOf(device) ?: -1
                        if (position != -1) {
                            adapter.getItem(position)?.id = macAddress
                            adapter.notifyItemChanged(position)
                        }
                        mViewModel.setCurrentZoneId(currentZone?.id!!)
                        deviceAssociateFragment.dismiss()
                        if (adapter.data.none { TextUtils.equals("0", it.id) }) Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
                    } else if (it?.status == Status.ERROR) {
                        deviceAssociateFragment.dismiss()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                })
            }
        }
    }

    override fun associationProgress(progress: Int) {
        if (progress in 0..99) {
            deviceAssociateFragment.onAssociateProgressChanged(progress)
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let {
            if (TextUtils.equals("0", it.id)) {
                deviceAssociateFragment.isCancelable = false
                deviceAssociateFragment.show(fragmentManager, "DeviceAssociateFragment")
                if (TextUtils.equals("LinkupHome V1", it.name)) {
                    listener.associateDevice(it.hash, it.macAddress)
                } else {
                    listener.associateDevice(it.hash, null)
                }
            }
        }
    }

    override fun onCheckedChanged(singleDevice: Device, isChecked: Boolean) {
        LightControllerFactory().createCommonController(singleDevice)?.setOnOff(isChecked)
        changeDeviceState(singleDevice, "on", if (isChecked) "1" else "0")
    }

    override fun onProgressChanged(singleDevice: Device, progress: Int) {
        LightControllerFactory().createCommonController(singleDevice)?.setBrightness(if (singleDevice.type == 6 || singleDevice.type == 10) progress else progress.plus(15))
        changeDeviceState(singleDevice, "brightness", progress.toString())
    }


    interface DevicesStateListener {
        fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener?)
        fun associateDevice(uuidHash: String, macAddress: String?)
    }

    private fun changeDeviceState(device: Device, key: String, value: String) {
        updateState(device, key, value)
        context?.getIMEI()?.let { it1 ->
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
