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
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.vo.RemoveDeviceVo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.navigator.ControlFragmentNavigator
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeleteDeviceListener
import com.ihomey.linkuphome.listener.DeviceRemoveListener
import com.ihomey.linkuphome.listener.FragmentVisibleStateListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.spp.BluetoothSPP
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.devices_fragment.*
import kotlinx.android.synthetic.main.view_device_list_empty.*

open class DeviceFragment : BaseFragment(), FragmentVisibleStateListener, DeviceRemoveListener, DeleteDeviceListener, DeviceListAdapter.OnItemClickListener, DeviceListAdapter.OnItemChildClickListener, DeviceListAdapter.OnCheckedChangeListener, DeviceListAdapter.OnSeekBarChangeListener {

    companion object {
        fun newInstance() = DeviceFragment()
    }

    protected lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: DeviceListAdapter
    private lateinit var meshServiceStateListener: MeshServiceStateListener
    private var isUserTouch: Boolean = false
    private var deviceList: List<Device>? = null

    private val navigator:ControlFragmentNavigator=ControlFragmentNavigator()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.devicesResult.observe(viewLifecycleOwner, Observer<PagedList<Device>> {
            deviceList = it.snapshot()
            if (!isUserTouch) adapter.submitList(it)
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


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        meshServiceStateListener = context as MeshServiceStateListener

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigator.bind(NavHostFragment.findNavController(this@DeviceFragment))

        parentFragment?.parentFragment?.let {
            val baseNavHostFragment = (it as DeviceNavHostFragment)
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
        btn_add_device?.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment) }
        iv_add.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment) }
    }

    override fun onFragmentVisibleStateChanged(isVisible: Boolean) {
        if (!isVisible) isUserTouch = false
    }


    override fun deleteDevice(id: String, instructId: Int, pid: Int) {
        if (id.contains(":")) {
            isUserTouch = false
            if (instructId == 0) {
                BluetoothSPP.getInstance()?.disconnect(id)
                mViewModel.deleteM1Device(id)
            }
        } else {
            context?.getIMEI()?.let { it1 ->
                mViewModel.deleteDevice(it1, id).observe(viewLifecycleOwner, Observer<Resource<Boolean>> {
                    if (it?.status == Status.SUCCESS) {
                        mViewModel.setRemoveDeviceVo(RemoveDeviceVo(id, instructId, pid, this))
                    } else if (it?.status == Status.ERROR) {
                        hideLoadingView()
                        if(TextUtils.equals("0040",it.message)){
                            isUserTouch = false
                            mViewModel.deleteDevice(id)
                        }else{
                            it.message?.let { it2 -> activity?.toast(it2) }
                        }
                    } else if (it?.status == Status.LOADING) {
                        showLoadingView()
                    }
                })
            }
        }
    }

    override fun onItemChildClick(singleDevice: Device, view: View) {
        if (view.id == R.id.btn_delete) {
            val dialog = DeleteDeviceFragment()
            val bundle = Bundle()
            bundle.putString("deviceId", singleDevice.id)
            bundle.putInt("deviceInstructId", singleDevice.instructId)
            bundle.putInt("devicePId", singleDevice.pid)
            dialog.arguments = bundle
            dialog.setDeleteDeviceListener(this)
            dialog.show(fragmentManager, "DeleteDeviceFragment")
        }
    }


    override fun onItemClick(singleDevice: Device) {
        mViewModel.setCurrentControlDevice(singleDevice)
//        when (singleDevice.type) {
//            1 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_c3ControlFragment)
//            2 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_r2ControlFragment)
//            3 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_a2ControlFragment)
//            4 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_n1ControlFragment)
//            0 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_m1ControlFragment)
//            6 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_v1ControlFragment)
//            7 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_s1ControlFragment)
//            8 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_s2ControlFragment)
//            9 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_t1ControlFragment)
//            10 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_v2ControlFragment)
//        }
        navigator.openDeviceControlPage()
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
            LightControllerFactory().createCommonController(singleDevice)?.setBrightness(if (singleDevice.type == 6 || singleDevice.type == 10) progress else progress.plus(15))
            changeDeviceState(singleDevice, "brightness", progress.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
        context?.getIMEI()?.let { it1 ->
            mViewModel.changeDeviceState(it1, device.id, key, value).observe(viewLifecycleOwner, Observer<Resource<Device>> {

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
