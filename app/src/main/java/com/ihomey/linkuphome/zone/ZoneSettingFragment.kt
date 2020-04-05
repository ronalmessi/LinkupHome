package com.ihomey.linkuphome.zone

import android.content.Context
import android.graphics.Color
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
import com.ihomey.linkuphome.adapter.ZoneListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.device.DeleteDevicesFragment
import com.ihomey.linkuphome.dialog.ConfirmDialogFragment
import com.ihomey.linkuphome.dialog.HintDialogFragment
import com.ihomey.linkuphome.dialog.InputDialogFragment
import com.ihomey.linkuphome.getDeviceId
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.ConfirmDialogInterface
import com.ihomey.linkuphome.listener.InputDialogInterface
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import kotlinx.android.synthetic.main.zone_setting_fragment.*

class ZoneSettingFragment : BaseFragment(), DeleteDevicesFragment.ConfirmButtonClickListener, OnItemMenuClickListener, InputDialogInterface, ConfirmDialogInterface, ZoneListAdapter.OnItemChildClickListener, ZoneListAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ZoneSettingFragment()
    }

    private lateinit var bridgeListener: BridgeListener
    private lateinit var viewModel: ZoneSettingViewModel
    private lateinit var adapter: ZoneListAdapter
    private lateinit var mViewModel: HomeActivityViewModel
    private var selectedZone: Zone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        parentFragment?.parentFragment?.let {
            viewModel = ViewModelProviders.of(it).get(ZoneSettingViewModel::class.java)
            viewModel.getLocalZones().observe(viewLifecycleOwner, Observer<PagedList<Zone>> {
                adapter.submitList(it)
            })
            loadRemoteZones()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        bridgeListener = context as BridgeListener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ZoneListAdapter()
        adapter.setOnItemChildClickListener(this)
        adapter.setOnItemClickListener(this)
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(LinearLayoutManager.HORIZONTAL, context?.resources?.getDimension(R.dimen._63sdp)?.toInt()!!, it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_zone_list.addItemDecoration(it) }
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, position ->
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val viewType = adapter.getItemViewType(position)
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(if (viewType == 0) context?.resources?.getDimension(R.dimen._72sdp)?.toInt()!! else context?.resources?.getDimension(R.dimen._108sdp)?.toInt()!!).setHeight(height).setText(if (viewType == 0) R.string.action_delete else R.string.title_quit_zone).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }
        rcv_zone_list.setSwipeMenuCreator(swipeMenuCreator)
        rcv_zone_list.setOnItemMenuClickListener(this)
        rcv_zone_list.adapter = adapter

        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }

        btn_create_zone.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isDefault", false)
            Navigation.findNavController(it).navigate(R.id.action_zoneSettingFragment_to_createZoneFragment2, bundle)
        }
        btn_join_zone.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_zoneSettingFragment_to_joinZoneFragment)
        }
        btn_share_zone.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_zoneSettingFragment_to_shareZoneListFragment)
        }
        parentFragment?.parentFragment?.let { (it as BaseNavHostFragment).showBottomNavigationBar(false) }

    }

    private fun loadRemoteZones() {
        context?.getDeviceId()?.let { it1 ->
            viewModel.getRemoteZones(it1).observe(viewLifecycleOwner, Observer<Resource<List<Zone>>> {
                when {
                    it?.status == Status.LOADING -> showLoadingView()
                    it?.status == Status.SUCCESS -> hideLoadingView()
                    it?.status == Status.ERROR -> {
                        hideLoadingView()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                }
            })
        }

    }


    override fun onItemClick(position: Int) {
        adapter.currentList?.get(position)?.let {
            if (it.active == 0) {
                context?.getDeviceId()?.let { it1 ->
                    viewModel.switchZone(it1, it.id).observe(viewLifecycleOwner, Observer<Resource<ZoneDetail>> {
                        when {
                            it?.status == Status.SUCCESS -> {
                                hideLoadingView()
                                bridgeListener.reConnectBridge()
                                mViewModel.setCurrentZoneId(it.data?.id)
                                Navigation.findNavController(iv_back).popBackStack()
                            }
                            it?.status == Status.ERROR -> {
                                hideLoadingView()
                                it.message?.let { it2 -> activity?.toast(it2) }
                            }
                            it?.status == Status.LOADING -> showLoadingView()
                        }
                    })
                }
            }
        }
    }

    override fun onItemChildClick(position: Int, view: View) {
        adapter.currentList?.get(position)?.let {
            selectedZone = it
            val dialog = InputDialogFragment()
            val bundle = Bundle()
            bundle.putString("title", getString(R.string.title_rename))
            bundle.putString("inputText", it.name)
            dialog.arguments = bundle
            dialog.setInputDialogInterface(this)
            dialog.show(fragmentManager, "InputDialogFragment")
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, position: Int) {
        if (adapter.itemCount == 1) {
            val hintDialogFragment = HintDialogFragment()
            hintDialogFragment.isCancelable = false
            val bundle = Bundle()
            bundle.putString("hintText", getString(R.string.msg_minimum_zone))
            hintDialogFragment.arguments = bundle
            hintDialogFragment.show(fragmentManager, "HintDialogFragment")
        } else {
            adapter.currentList?.get(position)?.let { it0 ->
                selectedZone = it0
                if (it0.type == 1) {
                    val dialog = ConfirmDialogFragment()
                    val bundle = Bundle()
                    bundle.putString("title", getString(R.string.msg_notes))
                    bundle.putString("content", getString(R.string.msg_quit_shared_zone))
                    dialog.arguments = bundle
                    dialog.setConfirmDialogInterface(this)
                    dialog.show(fragmentManager, "ConfirmDialogFragment")
                } else {
                    context?.getDeviceId()?.let { it1 ->
                        viewModel.getZone(it1, it0.id).observe(viewLifecycleOwner, Observer<Resource<ZoneDetail>> {
                            if (it?.status == Status.SUCCESS) {
                                hideLoadingView()
                                if (it.data?.devices.isNullOrEmpty()) {
                                    deleteZone(it0.id)
                                } else {
                                    val deleteDevicesFragment = DeleteDevicesFragment()
                                    deleteDevicesFragment.isCancelable = false
                                    deleteDevicesFragment.setConfirmButtonClickListener(this)
                                    val bundle = Bundle()
                                    bundle.putInt("zoneId", it0.id)
                                    deleteDevicesFragment.arguments = bundle
                                    deleteDevicesFragment.show(fragmentManager, "DeleteZoneFragment")
                                }
                            } else if (it?.status == Status.ERROR) {
                                hideLoadingView()
                                it.message?.let { it2 -> activity?.toast(it2) }
                            } else if (it?.status == Status.LOADING) {
                                showLoadingView()
                            }
                        })
                    }
                }
            }
        }
        menuBridge?.closeMenu()
    }

    override fun confirm(id: Int) {
        mViewModel.setRemoveDeviceFlag(true)
        context?.getDeviceId()?.let { it1 ->
            viewModel.switchZone(it1, id).observe(viewLifecycleOwner, Observer<Resource<ZoneDetail>> {
                when {
                    it?.status == Status.SUCCESS -> {
                        hideLoadingView()
                        bridgeListener.reConnectBridge()
                        mViewModel.setCurrentZoneId(it.data?.id)
                        Navigation.findNavController(btn_share_zone).popBackStack()
                    }
                    it?.status == Status.ERROR -> {
                        hideLoadingView()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                    it?.status == Status.LOADING -> showLoadingView()
                }
            })
        }
    }

    private fun deleteZone(zoneId: Int) {
        context?.getDeviceId()?.let { it1 ->
            viewModel.deleteZone(it1, zoneId).observe(viewLifecycleOwner, Observer<Resource<Int>> {
                when {
                    it?.status == Status.SUCCESS -> {
                        hideLoadingView()
                        it.data?.let {
                            bridgeListener.reConnectBridge()
                            mViewModel.setCurrentZoneId(it)
                        }
                    }
                    it?.status == Status.ERROR -> {
                        hideLoadingView()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                    it?.status == Status.LOADING -> showLoadingView()
                }
            })
        }
    }

    override fun onConfirmButtonClick() {
        selectedZone?.let {
            deleteZone(it.id)
        }
    }

    override fun onInput(text: String) {
        selectedZone?.let {
            context?.getDeviceId()?.let { it1 ->
                viewModel.changeZoneName(it1, it.id, text).observe(viewLifecycleOwner, Observer<Resource<Zone>> {
                    when {
                        it?.status == Status.SUCCESS -> {
                            hideLoadingView()
                            mViewModel.setCurrentZoneId(it.data?.id)
                        }
                        it?.status == Status.ERROR -> {
                            hideLoadingView()
                            it.message?.let { it2 -> activity?.toast(it2) }
                        }
                        it?.status == Status.LOADING -> showLoadingView()
                    }
                })
            }
        }
    }
}
