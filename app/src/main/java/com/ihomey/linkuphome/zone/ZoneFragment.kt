package com.ihomey.linkuphome.zone

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import cn.iclass.guideview.GuideBuilder
import com.chad.library.adapter.base.BaseQuickAdapter
import com.daimajia.swipe.SwipeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.RoomListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.RoomAndDevices
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device1.ColorCyclingSettingFragment
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeleteSubZoneListener
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.FragmentVisibleStateListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.room.DeleteRoomFragment
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.zone_fragment.*

class ZoneFragment : BaseFragment(),FragmentBackHandler, BaseQuickAdapter.OnItemChildClickListener, DeleteSubZoneListener, BaseQuickAdapter.OnItemClickListener, RoomListAdapter.OnCheckedChangeListener, RoomListAdapter.OnSeekBarChangeListener, FragmentVisibleStateListener {

    companion object {
        fun newInstance() = ZoneFragment()
    }

    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: RoomListAdapter
    private lateinit var meshServiceStateListener: MeshServiceStateListener
    private var guide: Guide? = null
    private var isFragmentVisible = false

    var hasShowBindDeviceGuide by PreferenceHelper("hasShowBindDeviceGuide", false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        meshServiceStateListener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                tv_title.text = it.data?.name
            }
        })
        mViewModel.roomsResult.observe(this, Observer<Resource<List<RoomAndDevices>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data)
                if (it.data != null && !it.data.isEmpty()) iv_add.visibility = View.VISIBLE else iv_add.visibility = View.INVISIBLE
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as ZoneNavHostFragment).showBottomNavigationBar(true) }
        adapter = RoomListAdapter(R.layout.item_sub_zone_list)
        adapter.onItemChildClickListener = this
        adapter.onItemClickListener = this
        adapter.setOnCheckedChangeListener(this)
        adapter.setOnSeekBarChangeListener(this)
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_zone_list.addItemDecoration(it) }
        rcv_zone_list.adapter = adapter
        adapter.setEmptyView(R.layout.view_zone_list_empty, rcv_zone_list)
        adapter.emptyView?.findViewById<FloatingActionButton>(R.id.btn_create_zone)?.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_zoneFragment_to_chooseZoneTypeFragment)
        }
        iv_add.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_zoneFragment_to_chooseZoneTypeFragment)
        }
        val baseNavHostFragment = parentFragment?.parentFragment as ZoneNavHostFragment
        baseNavHostFragment.setFragmentVisibleStateListener(this)
    }

    override fun onFragmentVisibleStateChanged(isVisible: Boolean) {
        isFragmentVisible = isVisible
        if (!hasShowBindDeviceGuide && isVisible && adapter.emptyViewCount == 0 && adapter.itemCount > 0) {
            rcv_zone_list.post{ rcv_zone_list.layoutManager?.findViewByPosition(0)?.let { showGuideView(it) } }
        }
    }

    override fun onResume() {
        super.onResume()
        rcv_zone_list.postDelayed({
            if (!hasShowBindDeviceGuide&&isFragmentVisible && adapter.emptyViewCount == 0 && adapter.itemCount > 0) {
                rcv_zone_list.layoutManager?.findViewByPosition(0)?.let { showGuideView(it) }
            }
        }, 250)
    }

    override fun deleteSubZone(id: Int) {
        context?.getIMEI()?.let { it1 ->
            mViewModel.deleteRoom(it1, id).observe(viewLifecycleOwner, Observer<Resource<Boolean>> {
                if (it?.status == Status.SUCCESS) {

                } else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val roomAndDevices = adapter.getItem(position)
        if (roomAndDevices != null) {
            when (view.id) {
                R.id.btn_delete -> {
                    roomAndDevices.room?.let { it ->
                        val dialog = DeleteRoomFragment()
                        val bundle = Bundle()
                        bundle.putInt("zoneId", it.id)
                        dialog.arguments = bundle
                        dialog.setDeleteSubZoneListener(this)
                        dialog.show(fragmentManager, "DeleteRoomFragment")
                        (view.parent as SwipeLayout).close(true)
                    }
                }

                R.id.btn_add, R.id.tv_sub_zone_name -> {
                    hideGuideView()
                    mViewModel.setSelectedRoom(roomAndDevices)
                    Navigation.findNavController(view).navigate(R.id.action_tab_zones_to_subZoneFragment)
                }

                R.id.iv_color_cycling -> {
                    val dialog = ColorCyclingSettingFragment()
                    mViewModel.setSelectedRoom(roomAndDevices)
                    dialog.show(fragmentManager, "ColorCyclingSettingFragment")
                }

                R.id.iv_lighting -> {
                    if (isFragmentVisible) {
                        for (index in roomAndDevices.devices.indices) {
                            val device = roomAndDevices.devices[index]
                            val controller = ControllerFactory().createController(device.type)
                            if (meshServiceStateListener.isMeshServiceConnected()) {
                                Handler().postDelayed({ controller?.setLightingMode(device.instructId) }, 100L * index)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        hideGuideView()
        adapter.getItem(position)?.let {
            mViewModel.setSelectedRoom(it)
            Navigation.findNavController(view).navigate(R.id.action_tab_zones_to_subZoneFragment)
        }
    }

    override fun onProgressChanged(item: RoomAndDevices, progress: Int) {
        if (isFragmentVisible) {
            for (index in item.devices.indices) {
                val device = item.devices[index]
                val controller = ControllerFactory().createController(device.type)
                if (meshServiceStateListener.isMeshServiceConnected()) {
                    Handler().postDelayed({ controller?.setLightBright(device.instructId, progress.plus(15)) }, 100L * index)
                }
            }
            item.room?.let { changeRoomState(it, "brightness", progress.toString()) }
        }
    }

    override fun onCheckedChanged(item: RoomAndDevices, isChecked: Boolean) {
        if (isFragmentVisible) {
            for (index in item.devices.indices) {
                val device = item.devices[index]
                val controller = ControllerFactory().createController(device.type)
                if (meshServiceStateListener.isMeshServiceConnected()) {
                    Handler().postDelayed({ controller?.setLightPowerState(device.instructId, if (isChecked) 1 else 0) }, 100L * index)
                }
            }
            item.room?.let { changeRoomState(it, "on", if (isChecked) "1" else "0") }
        }
    }

    override fun onBackPressed(): Boolean {
        return if (guide != null && guide?.isVisible!!) {
            hideGuideView()
            true
        }else{
            false
        }
    }

    private fun showGuideView(view: View) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(context?.resources?.getDimension(R.dimen._6sdp)?.toInt()!!)
                .setHighTargetMarginTop(getMarginTop(rcv_zone_list)+context?.resources?.getDimension(R.dimen._12sdp)?.toInt()!!)
                .setAutoDismiss(false)
                .setOverlayTarget(false)
                .setOutsideTouchable(false)
        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {
                hasShowBindDeviceGuide = true
            }
            override fun onDismiss() {}
        })
        builder.addComponent(object : Component {
            override fun getView(inflater: LayoutInflater): View {
                return inflater.inflate(R.layout.view_guide_bind_device, null)
            }

            override fun getAnchor(): Int {
                return Component.ANCHOR_BOTTOM
            }

            override fun getFitPosition(): Int {
                return Component.FIT_CENTER
            }

            override fun getXOffset(): Int {
                return 0
            }

            override fun getYOffset(): Int {
                return context?.resources?.getDimension(R.dimen._8sdp)?.toInt()!!
            }

        })
        if(guide==null){
            guide = builder.createGuide()
            guide?.setShouldCheckLocInWindow(true)
        }
        if(!guide?.isVisible!!) guide?.show(context as Activity)
    }

    private fun getMarginTop(view: View): Int {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        return loc[1]
    }

    private fun hideGuideView() {
        if (guide != null && guide?.isVisible!!) {
            guide?.dismiss()
        }
    }

    private fun changeRoomState(room: Room, key: String, value: String) {
        context?.getIMEI()?.let { it1 ->
            mViewModel.changeRoomState(it1, room.id, key, value).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                if (it?.status == Status.SUCCESS) {

                } else if (it?.status == Status.ERROR) {
//                it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

}
