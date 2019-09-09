package com.ihomey.linkuphome.device1

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.getShortName
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeviceAssociateListener
import kotlinx.android.synthetic.main.search_device_hint_fragment.*

class SearchDeviceHintFragment : BaseFragment(), DeviceAssociateListener {

    companion object {
        fun newInstance() = SearchDeviceHintFragment()
    }

    private lateinit var listener: ConnectDeviceFragment.DevicesStateListener
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_device_hint_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as ConnectDeviceFragment.DevicesStateListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        btn_device_reset.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_searchDeviceHintFragment_to_resetDeviceFragment2)
        }
        btn_next.setOnClickListener {
            val bundle = Bundle()
            arguments?.getInt("deviceType")?.let { it1 -> bundle.putInt("deviceType", it1) }
            arguments?.getInt("zoneId")?.let { it1 -> bundle.putInt("zoneId", it1) }
            Navigation.findNavController(it).navigate(R.id.action_searchDeviceHintFragment_to_searchDeviceFragment2, bundle)
        }
        listener.discoverDevices(true, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.clearScanedDevice()
    }


    override fun onDeviceFound(uuidHash: String, macAddress: String, name: String) {
        val type = arguments?.getInt("deviceType")!!
        val deviceType = DeviceType.values()[type]
        val deviceShortName = getShortName(deviceType)
        if(type!=6&&TextUtils.equals(deviceShortName, name)){
            val singleDevice1=Device(type,deviceType.name)
            singleDevice1.hash=uuidHash
            viewModel.setScanDevice(singleDevice1)
        }else if(type==6&&name.contains("V1")){
            val singleDevice1=Device(type,name)
            singleDevice1.hash=uuidHash
            singleDevice1.macAddress=macAddress
            viewModel.setScanDevice(singleDevice1)
        }
    }

    override fun deviceAssociated(deviceId: Int, message: String) {

    }

    override fun deviceAssociated(deviceId: Int, uuidHash: Int, bitmap: Long) {

    }

    override fun associationProgress(progress: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        listener.discoverDevices(false, null)
    }
}
