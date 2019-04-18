package com.ihomey.linkuphome.device1

import android.content.Context
import android.os.Bundle
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
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.RemoveDeviceVo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.DeviceRemoveFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeleteDeviceListener
import com.ihomey.linkuphome.listener.DeviceRemoveListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.devices_fragment.*

open class DeviceFragment : BaseFragment(), DeviceRemoveListener, DeleteDeviceListener, DeviceListAdapter.OnItemClickListener, DeviceListAdapter.OnItemChildClickListener, DeviceListAdapter.OnCheckedChangeListener, DeviceListAdapter.OnSeekBarChangeListener {

    companion object {
        fun newInstance() = DeviceFragment()
    }

    protected lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: DeviceListAdapter
    private lateinit var meshServiceStateListener: MeshServiceStateListener
    private val deviceRemoveFragment = DeviceRemoveFragment()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.devicesResult.observe(viewLifecycleOwner, Observer<PagedList<SingleDevice>> {
            Log.d("aa","haha")
            adapter.submitList(it)
            if (it.size > 0) iv_add.visibility = View.VISIBLE else iv_add.visibility = View.INVISIBLE
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        meshServiceStateListener = context as MeshServiceStateListener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as DeviceNavHostFragment).showBottomNavigationBar(true) }
        adapter = DeviceListAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setOnItemChildClickListener(this)
        adapter.setOnCheckedChangeListener(this)
        adapter.setOnSeekBarChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
//        adapter.setEmptyView(R.layout.view_device_list_empty, rcv_device_list)
//        adapter.emptyView?.findViewById<Button>(R.id.btn_add_device)?.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment) }
        iv_add.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_devices_to_chooseDeviceTypeFragment) }
    }


    override fun deleteDevice(id: Int, instructId: Int) {
        deviceRemoveFragment.isCancelable = false
        deviceRemoveFragment.show(fragmentManager, "DeviceRemoveFragment")
        mViewModel.setRemoveDeviceVo(RemoveDeviceVo(id, instructId, this))
    }

    override fun onItemChildClick(singleDevice: SingleDevice, view: View) {
        if (view.id == R.id.btn_delete) {
            val dialog = DeleteDeviceFragment()
            val bundle = Bundle()
            bundle.putInt("deviceId", singleDevice.id)
            bundle.putInt("deviceInstructId", singleDevice.instructId)
            dialog.arguments = bundle
            dialog.setDeleteDeviceListener(this)
            dialog.show(fragmentManager, "DeleteDeviceFragment")
            (view.parent as SwipeLayout).close(true)
        }
    }


    override fun onItemClick(singleDevice: SingleDevice) {
        mViewModel.setCurrentControlDevice(singleDevice)
        when (singleDevice.type - 1) {
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


    override fun onCheckedChanged(position: Int, isChecked: Boolean) {
        adapter.currentList?.get(position)?.let {
            parentFragment?.parentFragment?.let {it1->
                if ((it1 as DeviceNavHostFragment).getPagePosition() == 0&&(it.parameters?.on==1)==isChecked) {
                    val controller = ControllerFactory().createController(it.type)
                    if (meshServiceStateListener.isMeshServiceConnected()) {
                        controller?.setLightPowerState(it.instructId, if (isChecked) 1 else 0)
                    }
                    changeDeviceState(it, "on", if (isChecked) "1" else "0")
                }
            }
        }
    }

    override fun onProgressChanged(position: Int, progress: Int) {
        adapter.currentList?.get(position)?.let {
            parentFragment?.parentFragment?.let {it1->
                if ((it1 as DeviceNavHostFragment).getPagePosition() == 0) {
                    val controller = ControllerFactory().createController(it.type)
                    if (meshServiceStateListener.isMeshServiceConnected()) {
                        controller?.setLightBright(it.instructId, progress.plus(15))
                    }
                    changeDeviceState(it, "brightness", progress.toString())
                }
            }
        }
    }


    override fun onDeviceRemoved(deviceId: Int, deviceInstructId: Int, success: Boolean) {
        context?.getIMEI()?.let { it1 ->
            mViewModel.deleteDevice(it1, deviceId).observe(viewLifecycleOwner, Observer<Resource<Boolean>> {
                if (it?.status == Status.SUCCESS) {
                    deviceRemoveFragment.dismiss()
                } else if (it?.status == Status.ERROR) {
                    deviceRemoveFragment.dismiss()
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

    private fun changeDeviceState(singleDevice: SingleDevice, key: String, value: String) {
        context?.getIMEI()?.let { it1 ->
            mViewModel.changeDeviceState(it1, singleDevice.id, key, value).observe(viewLifecycleOwner, Observer<Resource<SingleDevice>> {
                if (it?.status == Status.SUCCESS) {

                } else if (it?.status == Status.ERROR) {
//                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }
}
