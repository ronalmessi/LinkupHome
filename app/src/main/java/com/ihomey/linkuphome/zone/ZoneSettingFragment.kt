package com.ihomey.linkuphome.zone

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ZoneListAdapter
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.UpdateZoneNameListener
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import kotlinx.android.synthetic.main.zone_setting_fragment.*

class ZoneSettingFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener, UpdateZoneNameListener, BaseQuickAdapter.OnItemClickListener, SwipeMenuItemClickListener {

    companion object {
        fun newInstance() = ZoneSettingFragment()
    }

    var currentZoneId by PreferenceHelper("currentZoneId", -1)

    private lateinit var viewModel: ZoneSettingViewModel
    private lateinit var adapter: ZoneListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(parentFragment!!).get(ZoneSettingViewModel::class.java)
        viewModel.getZones().observe(this, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
            }
        })
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
            bundle.putBoolean("isCurrent", false)
            Navigation.findNavController(it).navigate(R.id.action_zoneSettingFragment_to_createZoneFragment2, bundle)
        }
        btn_join_zone.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_zoneSettingFragment_to_joinZoneFragment)
        }
        btn_share_zone.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_zoneSettingFragment_to_shareZoneListFragment)
        }
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(false)
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
        if (zone != null) {
            currentZoneId=zone.id
            Navigation.findNavController(view).popBackStack()
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, position: Int) {
        val zone = adapter.getItem(position)
        if (zone != null) {
            if (adapter.itemCount == 1) {
                val deleteZoneFragment = DeleteZoneFragment()
                deleteZoneFragment.isCancelable = false
                val bundle = Bundle()
                bundle.putString("hintText", getString(R.string.zone_delete_hint))
                deleteZoneFragment.arguments = bundle
                deleteZoneFragment.show(fragmentManager, "DeleteZoneFragment")
            } else {
                viewModel.deleteZone(zone.id)
                currentZoneId=-1
            }
        }
        menuBridge?.closeMenu()
    }


    override fun updateZoneName(id: Int, newName: String) {
        currentZoneId=-1
        viewModel.updateZoneName(newName, id)
        currentZoneId=id
    }

}
