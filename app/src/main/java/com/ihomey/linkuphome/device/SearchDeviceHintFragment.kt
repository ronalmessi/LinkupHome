package com.ihomey.linkuphome.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.csrmesh.CSRMeshServiceManager
import com.ihomey.linkuphome.sigmesh.MeshDeviceScanListener
import com.ihomey.linkuphome.sigmesh.SigMeshServiceManager
import kotlinx.android.synthetic.main.search_device_hint_fragment.*

class SearchDeviceHintFragment : BaseFragment(), MeshDeviceScanListener {


    companion object {
        fun newInstance() = SearchDeviceHintFragment()
    }

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_device_hint_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        btn_device_reset.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_searchDeviceHintFragment_to_resetDeviceFragment) }
        btn_next.setOnClickListener {
            val bundle = Bundle()
            arguments?.getInt("deviceType")?.let { it1 -> bundle.putInt("deviceType", it1) }
            arguments?.getInt("zoneId")?.let { it1 -> bundle.putInt("zoneId", it1) }
            Navigation.findNavController(it).navigate(R.id.action_searchDeviceHintFragment_to_searchDeviceFragment, bundle)
        }
    }

    override fun onDeviceFound(device: Device) {
        arguments?.getInt("deviceType")?.let {
            if(it==device.type){
                viewModel.setScanDevice(device)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.clearScanedDevice()
    }

    override fun onStart() {
        super.onStart()
        CSRMeshServiceManager.getInstance().setMeshDeviceScanListener(this)
        CSRMeshServiceManager.getInstance().startScan()

        SigMeshServiceManager.getInstance().setMeshDeviceScanListener(this)
        SigMeshServiceManager.getInstance().startScan()
    }


    override fun onDestroy() {
        super.onDestroy()
        CSRMeshServiceManager.getInstance().setMeshDeviceScanListener(null)
        CSRMeshServiceManager.getInstance().stopScan()

        SigMeshServiceManager.getInstance().setMeshDeviceScanListener(null)
        SigMeshServiceManager.getInstance().stopScan()
    }
}
