package com.ihomey.linkuphome.zone

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.SubZoneListAdapter
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device1.ColorCyclingSettingFragment
import com.ihomey.linkuphome.device1.DevicesFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.DeleteSubZoneListener
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.zones_fragment.*

class ZonesFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener, DeleteSubZoneListener, BaseQuickAdapter.OnItemClickListener, SubZoneListAdapter.OnCheckedChangeListener, SubZoneListAdapter.OnSeekBarChangeListener {


    companion object {
        fun newInstance() = ZonesFragment()
    }

    private lateinit var viewModel: ZonesViewModel
    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: SubZoneListAdapter
    private lateinit var meshServiceStateListener: MeshServiceStateListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zones_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        meshServiceStateListener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ZonesViewModel::class.java)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getZones().observe(this, Observer<Resource<List<Zone>>> {
            if (it?.status == Status.SUCCESS&&it.data!=null) {
                val zone=it.data.find { it.isCurrent }
                tv_title.text = zone?.name
                zone?.let { it1 -> viewModel.setCurrentZone(it1) }
            }
        })
        viewModel.subZonesResult.observe(this, Observer<Resource<List<SubZoneModel>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
                if (it.data != null && !it.data.isEmpty()) iv_add.visibility = View.VISIBLE else iv_add.visibility = View.INVISIBLE
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(true)
        adapter = SubZoneListAdapter(R.layout.item_sub_zone_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener = this
        adapter.setOnCheckedChangeListener(this)
        adapter.setOnSeekBarChangeListener(this)
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_zone_list.addItemDecoration(it) }
        rcv_zone_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_zone_list_empty, rcv_zone_list)
        adapter.emptyView?.findViewById<FloatingActionButton>(R.id.btn_create_zone)?.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_zones_to_chooseZoneTypeFragment) }
        iv_add.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_tab_zones_to_chooseZoneTypeFragment) }
    }

    override fun deleteSubZone(id: Int) {
        viewModel.deleteSubZone(id)
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val subZoneModel = adapter.getItem(position)
        if (subZoneModel?.subZone != null) {
            when (view.id) {
                R.id.btn_delete -> {
                    val dialog = DeleteSubZoneFragment()
                    val bundle = Bundle()
                    subZoneModel.subZone?.id?.let { bundle.putInt("zoneId", it) }
                    dialog.arguments = bundle
                    dialog.setDeleteSubZoneListener(this)
                    dialog.show(fragmentManager, "DeleteSubZoneFragment")
                    (view.parent as SwipeLayout).close(true)
                }
                R.id.tv_sub_zone_name -> {
                    val bundle = Bundle()
                    subZoneModel.subZone?.id?.let { bundle.putInt("zoneId", it) }
                    subZoneModel.subZone?.device?.name?.let { bundle.putString("zoneName", it) }
                    Navigation.findNavController(view).navigate(R.id.action_tab_zones_to_subZoneFragment, bundle)
                }
                R.id.iv_color_cycling -> {
                    val dialog = ColorCyclingSettingFragment()
                    val bundle = Bundle()
                    subZoneModel.subZone?.id?.let { bundle.putInt("zoneId", it) }
                    subZoneModel.subZone?.sendTypes?.let { bundle.putInt("type", it.toInt()) }
                    dialog.arguments = bundle
                    dialog.show(fragmentManager, "ColorCyclingSettingFragment")
                }
                R.id.iv_lighting -> {
                    if (!TextUtils.isEmpty(subZoneModel.subZone?.sendTypes)) {
                        val deviceTypes = subZoneModel.subZone?.sendTypes?.split(",")
                        if (!deviceTypes.isNullOrEmpty()) {
                            for (deviceType in deviceTypes) {
                                val controller = ControllerFactory().createController(deviceType.toInt())
                                if (meshServiceStateListener.isMeshServiceConnected()) subZoneModel.subZone?.id?.let {
                                    Handler().postDelayed({ controller?.setLightingMode(it) }, (100 * deviceTypes.indexOf(deviceType)).toLong())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val subZoneModel = adapter.getItem(position)
        if (subZoneModel?.subZone != null) {
            val bundle = Bundle()
            subZoneModel.subZone?.id?.let { bundle.putInt("zoneId", it) }
            subZoneModel.subZone?.device?.name?.let { bundle.putString("zoneName", it) }
            Navigation.findNavController(view).navigate(R.id.action_tab_zones_to_subZoneFragment, bundle)
        }
    }

    override fun onProgressChanged(item: SubZoneModel, progress: Int) {
        if (!TextUtils.isEmpty(item.subZone?.sendTypes)) {
            val deviceTypes = item.subZone?.sendTypes?.split(",")
            if (!deviceTypes.isNullOrEmpty()) {
                for (deviceType in deviceTypes) {
                    val controller = ControllerFactory().createController(deviceType.toInt())
                    if (meshServiceStateListener.isMeshServiceConnected()) item.subZone?.id?.let {
                        val aa = (200 * deviceTypes.indexOf(deviceType)).toLong()
                        Handler().postDelayed({ controller?.setLightBright(it, progress.plus(15)) }, (100 * deviceTypes.indexOf(deviceType)).toLong())
                    }
                }
            }
            item.subZone?.state?.brightness = progress
            item.subZone?.let { viewModel.updateSubZone(it) }
        }
    }

    override fun onCheckedChanged(item: SubZoneModel, isChecked: Boolean) {
        if (!TextUtils.isEmpty(item.subZone?.sendTypes)) {
            val deviceTypes = item.subZone?.sendTypes?.split(",")
            if (!deviceTypes.isNullOrEmpty()) {
                for (deviceType in deviceTypes) {
                    val controller = ControllerFactory().createController(deviceType.toInt())
                    if (meshServiceStateListener.isMeshServiceConnected()) item.subZone?.id?.let {
                        Handler().postDelayed({ controller?.setLightPowerState(it, if (isChecked) 1 else 0) }, (100 * deviceTypes.indexOf(deviceType)).toLong())
                    }
                }
                item.subZone?.state?.on = if (isChecked) 1 else 0
                item.subZone?.let { viewModel.updateSubZone(it) }
            }
        }

    }

}
