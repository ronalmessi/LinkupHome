package com.ihomey.linkuphome.zone

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.RoomListAdapter
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device1.ColorCyclingSettingFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.BottomNavigationVisibilityListener
import com.ihomey.linkuphome.listener.DeleteSubZoneListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.room.DeleteRoomFragment
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.zones_fragment.*

class ZonesFragment : Fragment(), BaseQuickAdapter.OnItemChildClickListener, DeleteSubZoneListener, BaseQuickAdapter.OnItemClickListener, RoomListAdapter.OnCheckedChangeListener, RoomListAdapter.OnSeekBarChangeListener {

    companion object {
        fun newInstance() = ZonesFragment()
    }

    private lateinit var listener: BottomNavigationVisibilityListener
    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: RoomListAdapter
    private lateinit var meshServiceStateListener: MeshServiceStateListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zones_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        meshServiceStateListener = context as MeshServiceStateListener
        listener = context as BottomNavigationVisibilityListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                tv_title.text = it.data?.name
            }
        })
        mViewModel.roomsResult.observe(this, Observer<Resource<List<Room>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
                if (it.data != null && !it.data.isEmpty()) iv_add.visibility = View.VISIBLE else iv_add.visibility = View.INVISIBLE
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener.showBottomNavigationBar(true)
        adapter = RoomListAdapter(R.layout.item_sub_zone_list, rcv_zone_list)
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
        mViewModel.deleteRoom(id)
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val room = adapter.getItem(position)
        if (room != null) {
            when (view.id) {
                R.id.btn_delete -> {
                    val dialog = DeleteRoomFragment()
                    val bundle = Bundle()
                    room.id.let { bundle.putInt("zoneId", it) }
                    dialog.arguments = bundle
                    dialog.setDeleteSubZoneListener(this)
                    dialog.show(fragmentManager, "DeleteRoomFragment")
                    (view.parent as SwipeLayout).close(true)
                }
                R.id.tv_sub_zone_name -> {
                    adapter.hideGuideView()
                    mViewModel.setSelectedRoom(room)
                    Navigation.findNavController(view).navigate(R.id.action_tab_zones_to_subZoneFragment)
                }
                R.id.iv_color_cycling -> {
                    val dialog = ColorCyclingSettingFragment()
                    val bundle = Bundle()
                    room.id.let { bundle.putInt("zoneId", it) }
                    room.sendTypes?.let { bundle.putInt("type", it.toInt()) }
                    dialog.arguments = bundle
                    dialog.show(fragmentManager, "ColorCyclingSettingFragment")
                }
                R.id.iv_lighting -> {
                    if (!TextUtils.isEmpty(room.sendTypes)) {
                        val deviceTypes = room.sendTypes?.split(",")
                        if (!deviceTypes.isNullOrEmpty()) {
                            for (deviceType in deviceTypes) {
                                val controller = ControllerFactory().createController(deviceType.toInt())
                                if (meshServiceStateListener.isMeshServiceConnected()) room.id.let {
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
        adapter.hideGuideView()
        val room = adapter.getItem(position)
        if (room != null) {
            mViewModel.setSelectedRoom(room)
            Navigation.findNavController(view).navigate(R.id.action_tab_zones_to_subZoneFragment)
        }
    }

    override fun onProgressChanged(item: Room, progress: Int) {
        if (!TextUtils.isEmpty(item.sendTypes)) {
            val deviceTypes = item.sendTypes?.split(",")
            if (!deviceTypes.isNullOrEmpty()) {
                for (deviceType in deviceTypes) {
                    val controller = ControllerFactory().createController(deviceType.toInt())
                    if (meshServiceStateListener.isMeshServiceConnected()) item.id.let {
                        Handler().postDelayed({ controller?.setLightBright(it, progress.plus(15)) }, (100 * deviceTypes.indexOf(deviceType)).toLong())
                    }
                }
            }
            item.state.brightness = progress
            mViewModel.updateRoom(item)
        }
    }

    override fun onCheckedChanged(item: Room, isChecked: Boolean) {
        if (!TextUtils.isEmpty(item.sendTypes)) {
            val deviceTypes = item.sendTypes?.split(",")
            if (!deviceTypes.isNullOrEmpty()) {
                for (deviceType in deviceTypes) {
                    val controller = ControllerFactory().createController(deviceType.toInt())
                    if (meshServiceStateListener.isMeshServiceConnected()) item.id.let {
                        Handler().postDelayed({ controller?.setLightPowerState(it, if (isChecked) 1 else 0) }, (100 * deviceTypes.indexOf(deviceType)).toLong())
                    }
                }
                item.state.on = if (isChecked) 1 else 0
                mViewModel.updateRoom(item)
            }
        }
    }

}
