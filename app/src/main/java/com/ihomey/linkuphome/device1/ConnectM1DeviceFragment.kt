package com.ihomey.linkuphome.device1

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
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
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.adapter.ScanDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.controller.M1Controller
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.DeviceState
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
import com.ihomey.linkuphome.spp.BluetoothSPP
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.connect_device_fragment.*
import kotlinx.android.synthetic.main.connect_device_fragment.iv_back
import kotlinx.android.synthetic.main.m1_control_setting_fragment.*
import org.spongycastle.util.encoders.Hex


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
                currentZone?.id?.let { viewModel.setQuery(it,0) }
            }
        })
        viewModel.devicesResult.observe(viewLifecycleOwner, Observer<Resource<List<Device>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data?.onEach { it.macAddress=it.id })
                listener.discoverDevices(true, this)
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
        BluetoothSPP.getInstance()?.setBluetoothConnectionListener(mBluetoothConnectionListener)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setNewData(null)
        BluetoothSPP.getInstance()?.setBluetoothConnectionListener(null)
        listener.discoverDevices(false, null)
        countDownTimer.cancel()
    }

    override fun newAppearance(shortName: String, macAddress: String) {
        val singleDevice1 = Device(0,DeviceType.values()[0].name,macAddress)
        if(adapter.data.indexOf(singleDevice1) == -1) adapter.addData(singleDevice1)
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let {it0->
            if(TextUtils.equals("0",it0.id)){
                val pairedDeviceAddress=BluetoothSPP.getInstance()?.pairedDeviceAddress
                if(pairedDeviceAddress!=null&& pairedDeviceAddress.any {TextUtils.equals(it,it0.macAddress)}){
                    deviceAssociateFragment.isCancelable = false
                    deviceAssociateFragment.show(fragmentManager, "DeviceAssociateFragment")
                    countDownTimer.start()
                    BluetoothSPP.getInstance()?.connect(it0.macAddress)
                }else{
                    Navigation.findNavController(iv_back).navigate(R.id.action_connectM1DeviceFragment_to_m1InstructionFragment)
                }
            }
        }
    }


    override fun onCheckedChanged(singleDevice: Device, isChecked: Boolean) {
        val controller = ControllerFactory().createController(singleDevice.type)
        controller?.setLightPowerState(singleDevice.id, if (isChecked) 1 else 0)
    }

    override fun onProgressChanged(singleDevice: Device, progress: Int) {
        val controller = ControllerFactory().createController(singleDevice.type)
        controller?.setLightBright(singleDevice.id,progress.plus(15))
    }

    interface DevicesStateListener {
        fun discoverDevices(enabled: Boolean, listener: SppStateListener?)
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


    private val mBluetoothConnectionListener= object :BluetoothSPP.BluetoothConnectionListener{
        override fun onDeviceConnecting(name: String?, address: String?) {

        }

        override fun onDeviceConnected(name: String?, address: String) {
            M1Controller().syncTime(address)
            val device = Device(0,DeviceType.values()[0].name,address)
            val position = adapter.data.indexOf(device) ?: -1
            if (position != -1) {
                adapter.getItem(position)?.id=address
                adapter.notifyItemChanged(position)
            }
            viewModel.saveDevice(0,currentZone?.id!!,DeviceType.values()[0].name,address)
            mViewModel.setCurrentZoneId(currentZone?.id!!)
            countDownTimer.cancel()
            deviceAssociateFragment.onAssociateProgressChanged(0)
            deviceAssociateFragment.dismiss()
            if (adapter.data.none { TextUtils.equals("0",it.id) }) Navigation.findNavController(iv_back).popBackStack(R.id.tab_devices, false)
        }

        override fun onDeviceConnectFailed(name: String?, address: String?) {
            countDownTimer.cancel()
            deviceAssociateFragment.onAssociateProgressChanged(0)
            deviceAssociateFragment.dismiss()
            activity?.toast("未检测到床头灯信号，请确保床头灯已开启，并且处于可控制范围内。", Toast.LENGTH_SHORT)
        }
    }
}
