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
import com.ihomey.linkuphome.device1.DeleteDevicesFragment
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.UpdateZoneNameListener
import com.ihomey.linkuphome.setting.SettingNavHostFragment
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import kotlinx.android.synthetic.main.zone_setting_fragment.*

class ZoneSettingFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener, UpdateZoneNameListener, BaseQuickAdapter.OnItemClickListener, SwipeMenuItemClickListener, DeleteDevicesFragment.ConfirmButtonClickListener {

    companion object {
        fun newInstance() = ZoneSettingFragment()
    }

    private lateinit var bridgeListener: BridgeListener
    private lateinit var viewModel: ZoneSettingViewModel
    private lateinit var adapter: ZoneListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(parentFragment!!).get(ZoneSettingViewModel::class.java)
        viewModel.getLocalZones().observe(this, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
            }
        })
        context?.getIMEI()?.let { it1 ->  viewModel.getRemoteZones(it1).observe(this, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS) {
//                adapter.setNewData(it.data)
            }
        })}
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        bridgeListener= context as BridgeListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ZoneListAdapter(R.layout.item_zone_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener = this
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(context, LinearLayoutManager.VERTICAL, it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_zone_list.addItemDecoration(it) }
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context?.resources?.getDimension(R.dimen._72sdp)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width!!.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }
        rcv_zone_list.setSwipeMenuCreator(swipeMenuCreator)
        rcv_zone_list.setSwipeMenuItemClickListener(this)
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
//        val zone = adapter.getItem(position)
//        if (zone != null) {
//            bridgeListener.reConnectBridge()
//            currentZoneId = zone.id
//            Navigation.findNavController(view).popBackStack()
//        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, position: Int) {
        val zone = adapter.getItem(position)
        if (zone != null) {
            context?.getIMEI()?.let { it1 ->  viewModel.deleteZone(it1,zone.id).observe(viewLifecycleOwner, Observer<Resource<Boolean>> {
                if (it?.status == Status.SUCCESS) {
//                adapter.setNewData(it.data)
                }else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })}
//            if (adapter.itemCount == 1) {
//                val deleteZoneFragment = DeleteZoneFragment()
//                deleteZoneFragment.isCancelable = false
//                val bundle = Bundle()
//                bundle.putString("hintText", getString(R.string.zone_delete_hint1))
//                deleteZoneFragment.arguments = bundle
//                deleteZoneFragment.show(fragmentManager, "DeleteZoneFragment")
//            } else {
//                viewModel.getDevices(zone.id).observe(this, Observer<Resource<List<SingleDevice>>> {
//                    if (it?.status == Status.SUCCESS && it.data != null) {
//                        if (it.data.isNullOrEmpty()) {
//                            viewModel.deleteZone(zone.id)
//                        } else {
//                            val deleteDevicesFragment = DeleteDevicesFragment()
//                            deleteDevicesFragment.isCancelable = false
//                            deleteDevicesFragment.setConfirmButtonClickListener(this)
//                            val bundle = Bundle()
//                            bundle.putInt("zoneId", zone.id)
//                            deleteDevicesFragment.arguments = bundle
//                            deleteDevicesFragment.show(fragmentManager, "DeleteZoneFragment")
//                        }
//                    }
//                })
//            }
        }
        menuBridge?.closeMenu()
    }

    override fun confirm(id: Int) {
        val bundle = Bundle()
        bundle.putInt("zoneId", id)
        Navigation.findNavController(btn_share_zone).navigate(R.id.action_zoneSettingFragment_to_connectedDevicesFragment,bundle)
    }


    override fun updateZoneName(id: Int, newName: String) {
        context?.getIMEI()?.let { it1 ->  viewModel.changeZoneName(it1,id,newName).observe(viewLifecycleOwner, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {

            }else if (it?.status == Status.ERROR) {
                it.message?.let { it2 -> activity?.toast(it2) }
            }
        })}
    }

}
