package com.ihomey.linkuphome.zone

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ZoneListAdapter
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.device1.DeleteDevicesFragment
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.UpdateZoneNameListener
import com.ihomey.linkuphome.setting.SettingNavHostFragment
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import kotlinx.android.synthetic.main.zone_setting_fragment.*

class ZoneSettingFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener, UpdateZoneNameListener, BaseQuickAdapter.OnItemClickListener, DeleteDevicesFragment.ConfirmButtonClickListener, OnItemMenuClickListener {

    companion object {
        fun newInstance() = ZoneSettingFragment()
    }

    private lateinit var bridgeListener: BridgeListener
    private lateinit var viewModel: ZoneSettingViewModel
    private lateinit var adapter: ZoneListAdapter
    private lateinit var mViewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(parentFragment!!).get(ZoneSettingViewModel::class.java)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.getLocalZones().observe(this, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        bridgeListener = context as BridgeListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ZoneListAdapter(R.layout.item_zone_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener = this
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(LinearLayoutManager.HORIZONTAL, context?.resources?.getDimension(R.dimen._63sdp)?.toInt()!!,it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_zone_list.addItemDecoration(it) }
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context?.resources?.getDimension(R.dimen._72sdp)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width!!.toInt()).setHeight(height).setText(R.string.action_delete).setTextColor(Color.WHITE).setTextSize(14)
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
        parentFragment?.parentFragment?.let { (it as SettingNavHostFragment).showBottomNavigationBar(false) }
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val zone = adapter.getItem(position)
        if (zone != null) {
            val dialog = ReNameZoneFragment()
            val bundle = Bundle()
            bundle.putInt("zoneId", zone.id)
            bundle.putString("zoneName", zone.name)
            dialog.arguments = bundle
            dialog.setUpdateZoneNameListener(this)
            dialog.show(fragmentManager, "ReNameZoneFragment")
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val zone = adapter.getItem(position)
        if (zone != null && zone.active == 0) {
            context?.getIMEI()?.let { it1 ->
                viewModel.switchZone(it1, zone.id).observe(viewLifecycleOwner, Observer<Resource<ZoneDetail>> {
                    if (it?.status == Status.SUCCESS) {
                        mViewModel.setCurrentZoneId(it.data?.id)
                        bridgeListener.reConnectBridge()
                        Navigation.findNavController(iv_back).popBackStack()
                    } else if (it?.status == Status.ERROR) {
                        it.message?.let { it2 -> activity?.toast(it2) }
                    }
                })
            }
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, position: Int) {
        if (adapter.itemCount == 1) {
            val deleteZoneFragment = DeleteZoneFragment()
            deleteZoneFragment.isCancelable = false
            val bundle = Bundle()
            bundle.putString("hintText", getString(R.string.msg_delete_zone_hint))
            deleteZoneFragment.arguments = bundle
            deleteZoneFragment.show(fragmentManager, "DeleteZoneFragment")
        } else {
            adapter.getItem(position)?.let {it0->
                context?.getIMEI()?.let { it1 ->
                    viewModel.getZone(it1, it0.id).observe(viewLifecycleOwner, Observer<Resource<ZoneDetail>> {
                        if (it?.status == Status.SUCCESS) {
                            if(it.data?.devices.isNullOrEmpty()){
                                deleteZone(it0.id)
                            }else{
                                val deleteDevicesFragment = DeleteDevicesFragment()
                                deleteDevicesFragment.isCancelable = false
                                deleteDevicesFragment.setConfirmButtonClickListener(this)
                                val bundle = Bundle()
                                bundle.putInt("zoneId", it0.id)
                                deleteDevicesFragment.arguments = bundle
                                deleteDevicesFragment.show(fragmentManager, "DeleteZoneFragment")
                            }
                        } else if (it?.status == Status.ERROR) {
                            it.message?.let { it2 -> activity?.toast(it2) }
                        }
                    })
                }
            }
        }
        menuBridge?.closeMenu()
    }

    override fun confirm(id: Int) {
        mViewModel.setRemoveDeviceFlag(true)
        context?.getIMEI()?.let { it1 ->
            viewModel.switchZone(it1, id).observe(viewLifecycleOwner, Observer<Resource<ZoneDetail>> {
                if (it?.status == Status.SUCCESS) {
                    mViewModel.setCurrentZoneId(it.data?.id)
                    bridgeListener.reConnectBridge()
                    Navigation.findNavController(btn_share_zone).popBackStack()
                } else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

     fun deleteZone(zoneId: Int) {
         context?.getIMEI()?.let { it1 ->
             viewModel.deleteZone(it1, zoneId).observe(viewLifecycleOwner, Observer<Resource<Int>> {
                 if (it?.status == Status.SUCCESS) {
                     it.data?.let {
                         mViewModel.setCurrentZoneId(it)
                         bridgeListener.reConnectBridge()
                     }
                 } else if (it?.status == Status.ERROR) {
                     it.message?.let { it2 -> activity?.toast(it2) }
                 }
             })
         }
    }



    override fun updateZoneName(id: Int, newName: String) {
        context?.getIMEI()?.let { it1 ->
            viewModel.changeZoneName(it1, id, newName).observe(viewLifecycleOwner, Observer<Resource<Zone>> {
                if (it?.status == Status.SUCCESS) {
                    mViewModel.setCurrentZoneId(it.data?.id)
                } else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

}
