package com.ihomey.linkuphome.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.UnBondedDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.RoomAndDevices
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.dialog.ConfirmDialogFragment
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.ConfirmDialogInterface
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.unbonded_devices_fragment.*

class UnBondDevicesFragment : BaseFragment(),ConfirmDialogInterface {

    companion object {
        fun newInstance() = UnBondDevicesFragment()
    }

    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var mViewModel: UnBondDevicesViewModel
    private lateinit var adapter: UnBondedDeviceListAdapter
    private var room: Room? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.unbonded_devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(UnBondDevicesViewModel::class.java)
        viewModel.mSelectedRoom.observe(this, Observer<RoomAndDevices> {
            room = it.room
            mViewModel.setZoneId(it.room?.zoneId)
        })

        mViewModel.unBondedDevicesResult1.observe(viewLifecycleOwner, Observer<PagedList<Device>> {
            adapter.submitList(it)
        })
        mViewModel.isUnBondedDevicesListEmptyLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) emptyView.visibility = View.VISIBLE else emptyView.visibility = View.GONE
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UnBondedDeviceListAdapter()
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        rcv_device_list.adapter = adapter
        iv_back.setOnClickListener {
            if (adapter.getSelectedDevices().isEmpty()) Navigation.findNavController(it).popBackStack() else {
                val dialog = ConfirmDialogFragment()
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.title_save_modifications))
                bundle.putString("content", getString(R.string.msg_save_modifications))
                dialog.arguments = bundle
                dialog.setConfirmDialogInterface(this)
                dialog.show(fragmentManager, "ConfirmDialogFragment")
            }
        }
        btn_save.setOnClickListener { it1 ->
            if (adapter.getSelectedDevices().isEmpty()) {
                onConfirmButtonClick()
            } else {
                bindDevice()
            }
        }
    }

    override fun onConfirmButtonClick() {
        Navigation.findNavController(btn_save).popBackStack()
    }


    private fun bindDevice() {
        showLoadingView()
        room?.let { bindDevice(it.zoneId, it.id, adapter.getSelectedDevices().map { it.id }.joinToString(","), "add") }
    }


    private fun bindDevice(zoneId: Int, groupId: Int, deviceIds: String, act: String) {
        context?.getIMEI()?.let { it1 ->
            viewModel.bindDevice(it1, zoneId, groupId, deviceIds, act).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                if (it?.status == Status.SUCCESS) {
                    hideLoadingView()
                    Navigation.findNavController(btn_save).popBackStack()
                } else if (it?.status == Status.ERROR) {
                    hideLoadingView()
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }
}
