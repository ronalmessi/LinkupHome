package com.ihomey.linkuphome.device

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.devicecontrol.navigator.ControlFragmentNavigator
import com.ihomey.linkuphome.dialog.ConfirmDialogFragment
import com.ihomey.linkuphome.encodeBase64
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.ConfirmDialogInterface
import com.ihomey.linkuphome.listener.FragmentVisibleStateListener
import com.ihomey.linkuphome.protocol.csrmesh.CSRMeshServiceManager
import com.ihomey.linkuphome.protocol.sigmesh.MeshDeviceRemoveListener
import com.ihomey.linkuphome.protocol.sigmesh.SigMeshServiceManager
import com.ihomey.linkuphome.protocol.spp.BluetoothSPP
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.pairlink.sigmesh.lib.PlSigMeshService
import kotlinx.android.synthetic.main.devices_fragment.*
import kotlinx.android.synthetic.main.view_device_list_empty.*

open class DeviceFragment : BaseFragment(), FragmentVisibleStateListener, DeviceListAdapter.OnItemClickListener, DeviceListAdapter.OnItemChildClickListener, DeviceListAdapter.OnCheckedChangeListener, DeviceListAdapter.OnSeekBarChangeListener, ConfirmDialogInterface, MeshDeviceRemoveListener {

    companion object {
        fun newInstance() = DeviceFragment()
    }

    protected lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: DeviceListAdapter

    private var isUserTouch: Boolean = false
    private var deviceList: List<Device>? = null
    private var selectedDevice: Device? = null
    private var mCurrentZone: Zone? = null

    private val navigator: ControlFragmentNavigator = ControlFragmentNavigator()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                mCurrentZone = it.data
            }
        })
        mViewModel.devicesResult.observe(viewLifecycleOwner, Observer<PagedList<Device>> {
            deviceList = it.snapshot()
            if (!isUserTouch) adapter.submitList(it)
            deviceList?.filter { it.type==0 }?.let {
                rcv_device_list.scrollToPosition(0)
            }
            deviceList?.forEach {
                if (it.type == 0) {
                    BluetoothSPP.getInstance()?.autoConnect(it.id)
                }
            }
        })
        mViewModel.isDeviceListEmptyLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) {
                emptyView.visibility = View.VISIBLE
                iv_add.visibility = View.INVISIBLE
            } else {
                emptyView.visibility = View.GONE
                iv_add.visibility = View.VISIBLE
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator.bind(NavHostFragment.findNavController(this@DeviceFragment))
        parentFragment?.parentFragment?.let {
            val baseNavHostFragment = (it as BaseNavHostFragment)
            baseNavHostFragment.setFragmentVisibleStateListener(this)
            baseNavHostFragment.showBottomNavigationBar(true)
        }

        adapter = DeviceListAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setOnItemChildClickListener(this)
        adapter.setOnCheckedChangeListener(this)
        adapter.setOnSeekBarChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        (rcv_device_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        btn_add_device?.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment)
        }
        iv_add.setOnClickListener {
//            PlSigMeshService.getInstance().resetNode(1002.toShort())
            Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment)
        }
    }

    override fun onFragmentVisibleStateChanged(isVisible: Boolean) {
        if (!isVisible) isUserTouch = false
    }

    override fun onItemChildClick(singleDevice: Device, view: View) {
        if (view.id == R.id.btn_delete) {
            selectedDevice = singleDevice
            val dialog = ConfirmDialogFragment()
            val bundle = Bundle()
            bundle.putString("title", getString(R.string.action_delete))
            bundle.putString("content", getString(R.string.action_remove_device))
            dialog.arguments = bundle
            dialog.setConfirmDialogInterface(this)
            dialog.show(fragmentManager, "ConfirmDialogFragment")
        }
    }

    override fun onConfirmButtonClick() {
        selectedDevice?.let { it0 ->
            if (it0.type == 0) {
                isUserTouch = false
                BluetoothSPP.getInstance()?.disconnect(it0.id)
                mViewModel.deleteM1Device(it0.id)
            } else {
                context?.getIMEI()?.let { it1 ->
                    mViewModel.deleteDevice(it1, it0.id).observe(viewLifecycleOwner, Observer<Resource<Boolean>> {
                        if (it?.status == Status.SUCCESS) {
                            resetDevice(it0)
                        } else if (it?.status == Status.ERROR) {
                            hideLoadingView()
                            if (TextUtils.equals("0040", it.message)) {
                                isUserTouch = false
                                mViewModel.deleteDevice(it0.id)
                            } else {
                                it.message?.let { it2 -> activity?.toast(it2) }
                            }
                        } else if (it?.status == Status.LOADING) {
                            showLoadingView()
                        }
                    })
                }
            }
        }
    }

    override fun onItemClick(singleDevice: Device) {
        mViewModel.setCurrentControlDevice(singleDevice)
        navigator.openDeviceControlPage()
    }

    private fun resetDevice(device: Device) {
        if (device.instructId != 0 && device.pid == 0) {
            CSRMeshServiceManager.getInstance().resetDevice(device, this)
        } else if (device.instructId == 0 && device.pid != 0) {
            SigMeshServiceManager.getInstance().resetDevice(device, this)
        }
    }

    private fun isFragmentVisible(): Boolean {
        parentFragment?.parentFragment?.let {
            return (it as DeviceNavHostFragment).getPagePosition() == 0
        }
        return false
    }

    override fun onCheckedChanged(singleDevice: Device, isChecked: Boolean) {
        isUserTouch = true
        if (isFragmentVisible()) {
            LightControllerFactory().createCommonController(singleDevice)?.setOnOff(isChecked)
            changeDeviceState(singleDevice, "on", if (isChecked) "1" else "0")
        }
    }

    override fun onProgressChanged(singleDevice: Device, progress: Int) {
        isUserTouch = true
        if (isFragmentVisible()) {
            LightControllerFactory().createCommonController(singleDevice)?.setBrightness(progress)
            changeDeviceState(singleDevice, "brightness", progress.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BluetoothSPP.getInstance()?.stopAutoConnect()
        navigator.unbind()
        isUserTouch = false

    }

    override fun onDeviceRemoved(deviceId: String) {
        hideLoadingView()
        isUserTouch = false
        mViewModel.deleteDevice(deviceId)
    }


    private fun changeDeviceState(device: Device, key: String, value: String) {
        updateState(device, key, value)
        if(device.type!=0)context?.getIMEI()?.let { mViewModel.changeDeviceState(it, device.id, key, value).observe(viewLifecycleOwner, Observer<Resource<Device>> {}) }
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
