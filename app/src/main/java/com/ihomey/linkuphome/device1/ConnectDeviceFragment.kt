package com.ihomey.linkuphome.device1

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ScanDeviceListAdapter
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.entity.ZoneSetting
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device.DeviceAssociateFragment
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.getShortName
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listeners.DeviceAssociateListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.connect_device_fragment.*

class ConnectDeviceFragment : Fragment(), DeviceAssociateListener, BaseQuickAdapter.OnItemClickListener {


    companion object {
        fun newInstance() = ConnectDeviceFragment()
    }

    private lateinit var listener: DevicesStateListener
    //    private lateinit var viewModel: ConnectDeviceViewModel
    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: ScanDeviceListAdapter
    private val deviceAssociateFragment = DeviceAssociateFragment()

    private var currentSetting: Setting? = null
    private var currentZone: Zone? = null
    private val uuidHashArray: SparseArray<String> = SparseArray()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connect_device_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(ConnectDeviceViewModel::class.java)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getGlobalSetting().observe(this, Observer<Resource<Setting>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentSetting = it.data
            }
        })
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentZone = it.data
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as DevicesStateListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ScanDeviceListAdapter(R.layout.item_scan_device)
        adapter.onItemClickListener = this
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_scan_device_list_empty, rcv_device_list)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack(R.id.tab_devices, false) }
    }

    override fun onResume() {
        super.onResume()
        listener.discoverDevices(true, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener.discoverDevices(false, this)
        uuidHashArray.clear()
    }

    override fun newAppearance(uuidHash: Int, appearance: ByteArray, shortName: String) {
        val type = arguments?.getInt("deviceType")!!
        if (uuidHashArray.indexOfKey(uuidHash) < 0) {
            uuidHashArray.put(uuidHash, shortName)
            val deviceType = DeviceType.values()[type]
            val deviceShortName = getShortName(deviceType)
            if (TextUtils.equals(deviceShortName, shortName)) adapter.addData(SingleDevice(0, currentZone?.id!!, deviceType.name, type, uuidHash, 0, 0, 0))
        }
    }

    override fun deviceAssociated(deviceId: Int, message: String) {
        deviceAssociateFragment.onAssociateProgressChanged(0)
        deviceAssociateFragment.dismiss()
        activity?.toast(message)
    }

    override fun deviceAssociated(deviceId: Int, uuidHash: Int, bitmap: Long) {
        val type = arguments?.getInt("deviceType")!!
        val deviceType = DeviceType.values()[type]
        val device = SingleDevice(deviceId, currentZone?.id!!, deviceType.name, type, uuidHash, 0, bitmap, 0)
        val position = adapter.data.indexOf(device) ?: -1
        if (position != -1) {
            adapter.getItem(position)?.id = deviceId
            adapter.notifyItemChanged(position)
            mViewModel.addSingleDevice(currentSetting!!, device)
        }
        deviceAssociateFragment.dismiss()
        if (adapter.data.none { it.id == 0 }) Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
    }

    override fun associationProgress(progress: Int) {
        if (progress in 0..99) {
            deviceAssociateFragment.onAssociateProgressChanged(progress)
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val singleDevice = adapter.getItem(position)
        if (singleDevice?.id == 0) {
            deviceAssociateFragment.isCancelable = false
            deviceAssociateFragment.show(fragmentManager, "DeviceAssociateFragment")
            listener.associateDevice(singleDevice.hash, null)
        }
    }


    interface DevicesStateListener {
        fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener?)
        fun associateDevice(uuidHash: Int, shortCode: String?)
    }
}
