package com.ihomey.linkuphome.device1

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.adapter.ScanDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.DeviceAssociateFragment
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeviceAssociateListener
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.listener.SppStateListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.connect_device_fragment.*


class ConnectM1DeviceFragment : BaseFragment(),FragmentBackHandler, DeviceListAdapter.OnCheckedChangeListener, BaseQuickAdapter.OnItemClickListener, DeviceListAdapter.OnSeekBarChangeListener, SppStateListener {

    companion object {
        fun newInstance() = ConnectM1DeviceFragment()
    }

    private lateinit var listener:DevicesStateListener
    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var viewModel: ConnectDeviceViewModel
    private lateinit var adapter: ScanDeviceListAdapter
    private lateinit var countDownTimer: AssociateDeviceCountDownTimer
    private val deviceAssociateFragment = DeviceAssociateFragment()

    private var currentZone: Zone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.connect_device_fragment, container, false)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as DevicesStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ConnectDeviceViewModel::class.java)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentZone = it.data
                currentZone?.id?.let { viewModel.setQuery(it,5) }
            }
        })
        viewModel.devicesResult.observe(viewLifecycleOwner, Observer<Resource<List<Device>>> {
            if (it?.status == Status.SUCCESS&&(adapter.emptyViewCount==1&&adapter.itemCount==1)) {
                adapter.setNewData(it.data)

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
        countDownTimer = AssociateDeviceCountDownTimer(20000, 1000)
    }

    override fun onResume() {
        super.onResume()
        listener.discoverDevices(true, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener.discoverDevices(false, null)
        countDownTimer.cancel()
    }

    override fun newAppearance(shortName: String, macAddress: String) {
        val singleDevice1 = Device(5,DeviceType.values()[4].name,macAddress)
        if(adapter.data.indexOf(singleDevice1) == -1) adapter.addData(singleDevice1)
    }

    override fun deviceAssociated(isSuccess: Boolean, shortName: String, macAddress: String) {
        if(isSuccess){
            context?.getIMEI()?.let { it1 ->
                viewModel.saveDevice(it1, currentZone?.id!!, 5, DeviceType.values()[4].name,macAddress).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                    if (it?.status == Status.SUCCESS&&it.data!=null) {
                        it.data.macAddress=macAddress
                        val position = adapter.data.indexOf(it.data) ?: -1
                        if (position != -1) {
                            adapter.getItem(position)?.id=it.data.id
                            adapter.getItem(position)?.macAddress=macAddress
                            adapter.notifyItemChanged(position)
                        }
                        var currentDeviceAddress by PreferenceHelper("currentDeviceAddress", "")
                        currentDeviceAddress=macAddress
                        mViewModel.setCurrentZoneId(currentZone?.id!!)
                        countDownTimer.cancel()
                        deviceAssociateFragment.onAssociateProgressChanged(0)
                        deviceAssociateFragment.dismiss()
                        if (adapter.data.none { it.id == 0 }) Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
                    } else if (it?.status == Status.ERROR) {
                        deviceAssociateFragment.dismiss()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                })
            }
        }else{
            countDownTimer.cancel()
            deviceAssociateFragment.onAssociateProgressChanged(0)
            deviceAssociateFragment.dismiss()
            activity?.toast("M1 连接失败！", Toast.LENGTH_SHORT)
       }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let {
            if(it.id==0){
                it.macAddress?.let {
                    deviceAssociateFragment.isCancelable = false
                    deviceAssociateFragment.show(fragmentManager, "DeviceAssociateFragment")
                    countDownTimer.start()
                    listener.associateDevice(it)
                }
            }
        }
    }

    override fun onCheckedChanged(singleDevice: Device, isChecked: Boolean) {
        val controller = ControllerFactory().createController(singleDevice.type)
        controller?.setLightPowerState(0, if (isChecked) 1 else 0)
    }

    override fun onProgressChanged(singleDevice: Device, progress: Int) {
        val controller = ControllerFactory().createController(singleDevice.type)
        controller?.setLightBright(0,progress.plus(15))
    }

    interface DevicesStateListener {
        fun discoverDevices(enabled: Boolean, listener: SppStateListener?)
        fun associateDevice(macAddress: String)
    }

    private inner class AssociateDeviceCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            deviceAssociateFragment.onAssociateProgressChanged(0)
            deviceAssociateFragment.dismiss()
        }

        override fun onTick(millisUntilFinished: Long) {
            deviceAssociateFragment.onAssociateProgressChanged((20-(millisUntilFinished / 1000).toInt())*5)
        }
    }
}
