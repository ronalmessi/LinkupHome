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
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.vo.RemoveDeviceVo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.DeviceRemoveFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeleteDeviceListener
import com.ihomey.linkuphome.listener.DeviceRemoveListener
import com.ihomey.linkuphome.listener.FragmentVisibleStateListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
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
    private val deviceRemoveFragment = DeviceRemoveFragment()
    private var isUserTouch: Boolean=false
    private var deviceList:List<Device>?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.devicesResult.observe(viewLifecycleOwner, Observer<PagedList<Device>> {
            deviceList=it.snapshot()
            if(!isUserTouch) adapter.submitList(it)
        })
        mViewModel.isDeviceListEmptyLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            if(it){
                emptyView.visibility=View.VISIBLE
                iv_add.visibility = View.INVISIBLE
            } else{
                emptyView.visibility=View.GONE
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
        parentFragment?.parentFragment?.let {
            val baseNavHostFragment =(it as DeviceNavHostFragment)
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
        if(!isVisible) isUserTouch=false
    }


    override fun deleteDevice(id: Int, instructId: Int) {
        context?.getIMEI()?.let { it1 ->
            mViewModel.deleteDevice(it1, id).observe(viewLifecycleOwner, Observer<Resource<Boolean>> {
                if (it?.status == Status.SUCCESS) {
                    deviceRemoveFragment.isCancelable = false
                    deviceRemoveFragment.show(fragmentManager, "DeviceRemoveFragment")
                    mViewModel.setRemoveDeviceVo(RemoveDeviceVo(id, instructId, this))
                } else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

    override fun onItemChildClick(device: Device, view: View) {
        if (view.id == R.id.btn_delete) {
            val dialog = DeleteDeviceFragment()
            val bundle = Bundle()
            bundle.putInt("deviceId", device.id)
            bundle.putInt("deviceInstructId", device.instructId)
            dialog.arguments = bundle
            dialog.setDeleteDeviceListener(this)
            dialog.show(fragmentManager, "DeleteDeviceFragment")
        }
    }


    override fun onItemClick(position: Int) {
        deviceList?.let {
            val singleDevice=it[position]
            mViewModel.setCurrentControlDevice(singleDevice)
            when (singleDevice.type-1) {
                0 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_c3ControlFragment)
                1 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_r2ControlFragment)
                2 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_a2ControlFragment)
                3 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_n1ControlFragment)
                5 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_v1ControlFragment)
                6 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_s1ControlFragment)
                7 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_s2ControlFragment)
                8 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_t1ControlFragment)
                9 -> NavHostFragment.findNavController(this@DeviceFragment).navigate(R.id.action_tab_devices_to_v2ControlFragment)
            }
        }
    }


    private fun isFragmentVisible():Boolean{
        parentFragment?.parentFragment?.let {
            return (it as DeviceNavHostFragment).getPagePosition()==0
        }
        return false
    }

    override fun onCheckedChanged(position: Int, isChecked: Boolean) {
        isUserTouch=true
        adapter.currentList?.get(position)?.let {
            if(isFragmentVisible()){
                val controller = ControllerFactory().createController(it.type)
                if (meshServiceStateListener.isMeshServiceConnected()) {
                    controller?.setLightPowerState(it.instructId, if (isChecked) 1 else 0)
                }
                changeDeviceState(it, "on", if (isChecked) "1" else "0")
            }
        }
    }

    override fun onProgressChanged(position: Int, progress: Int) {
        isUserTouch=true
        adapter.currentList?.get(position)?.let {
            if(isFragmentVisible()){
                val controller = ControllerFactory().createController(it.type)
                if (meshServiceStateListener.isMeshServiceConnected()) {
                    controller?.setLightBright(it.instructId, if(it.type==5||it.type==9) progress.plus(5) else progress.plus(15))
                }
                changeDeviceState(it, "brightness", progress.toString())
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        isUserTouch=false
    }

    override fun onDeviceRemoved(deviceId: Int, uuidHash: Int, success: Boolean) {
        deviceRemoveFragment.dismiss()
        isUserTouch=false
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
        if(TextUtils.equals("brightness", key)){
            val deviceState = device.parameters
            deviceState?.let {
                it.brightness=value.toInt()
                mViewModel.updateDeviceState(device,it)
            }
        }else{
            val deviceState = device.parameters
            deviceState?.let {
                it.on=value.toInt()
                mViewModel.updateRoomAndDeviceState(device,it)
            }
        }
    }
}
