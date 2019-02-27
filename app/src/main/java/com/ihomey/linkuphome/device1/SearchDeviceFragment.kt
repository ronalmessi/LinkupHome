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
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.getShortName
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeviceAssociateListener
import kotlinx.android.synthetic.main.search_device_fragment.*

class SearchDeviceFragment : BaseFragment(), DeviceAssociateListener {
    override fun deviceAssociated(deviceId: Int, message: String) {

    }

    override fun deviceAssociated(deviceId: Int, uuidHash: Int, bitmap: Long) {

    }

    override fun associationProgress(progress: Int) {

    }


    companion object {
        fun newInstance() = SearchDeviceFragment()
    }

    private lateinit var listener: ConnectDeviceFragment.DevicesStateListener

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_device_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.getScanedDevice().observe(this, Observer<SingleDevice> {
            if (it != null) {
                val bundle = Bundle()
                bundle.putInt("deviceType", arguments?.getInt("deviceType")!!)
                Navigation.findNavController(iv_back).navigate(R.id.action_searchDeviceFragment_to_connectDeviceFragment2, bundle)
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as ConnectDeviceFragment.DevicesStateListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        btn_device_reset.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_searchDeviceFragment_to_resetDeviceFragment2) }
        iv_device_connect_lamp_icon.setImageResource(AppConfig.DEVICE_ICON[arguments?.getInt("deviceType", 1)!!])
        listener.discoverDevices(true, this)
    }

    override fun newAppearance(uuidHash: Int, appearance: ByteArray, shortName: String) {
        val type = arguments?.getInt("deviceType")!!
        val deviceType = DeviceType.values()[type]
        val deviceShortName = getShortName(deviceType)
        if (TextUtils.equals(deviceShortName, shortName)) viewModel.setScanedDevice(SingleDevice(0, arguments?.getInt("zoneId")!!, deviceType.name, type, uuidHash, 0, 0, 0))
    }
}
