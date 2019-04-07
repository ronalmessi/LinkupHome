package com.ihomey.linkuphome.room

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import cn.iclass.guideview.Component
import cn.iclass.guideview.GuideBuilder
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.BindedDeviceListAdapter
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.RoomAndDevices
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device1.ReNameDeviceFragment
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.group.GroupUpdateFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.GroupUpdateListener
import com.ihomey.linkuphome.listener.UpdateDeviceNameListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.ihomey.linkuphome.zone.ZoneNavHostFragment
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import kotlinx.android.synthetic.main.sub_zone_fragment.*


class RoomFragment : Fragment(), BindedDeviceListAdapter.OnCheckedChangeListener, SwipeMenuItemClickListener, BaseQuickAdapter.OnItemClickListener, UpdateDeviceNameListener {


    companion object {
        fun newInstance() = RoomFragment()
    }

    var hasShowRenameRoomGuide by PreferenceHelper("hasShowRenameRoomGuide", false)


    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var adapter: BindedDeviceListAdapter
    private lateinit var listener: MeshServiceStateListener

    private lateinit var room: Room

    private val mDialog: GroupUpdateFragment = GroupUpdateFragment()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sub_zone_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.mSelectedRoom.observe(this, Observer<Room> {
            tv_title.text = it?.name
            room = it
        })
        viewModel.devicesResult.observe(viewLifecycleOwner, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                adapter.setNewData(it.data?.filter { (it.roomId == room.id) })
                if (it.data?.filter { it.roomId == room.id }.isNullOrEmpty()) btn_add_device.visibility = View.GONE else btn_add_device.visibility = View.VISIBLE
            }
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as ZoneNavHostFragment).showBottomNavigationBar(false) }
        adapter = BindedDeviceListAdapter(R.layout.item_binded_device)
        adapter.onItemClickListener = this
        adapter.setOnCheckedChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context?.resources?.getDimension(R.dimen._72sdp)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width!!.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }
        rcv_device_list.setSwipeMenuCreator(swipeMenuCreator)
        rcv_device_list.setSwipeMenuItemClickListener(this)
        rcv_device_list.adapter = adapter

        adapter.setEmptyView(R.layout.view_device_list_empty, rcv_device_list)
        adapter.emptyView?.findViewById<Button>(R.id.btn_add_device)?.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_subZoneFragment_to_unBondedDevicesFragment2)

        }
        btn_add_device.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_subZoneFragment_to_unBondedDevicesFragment2)
        }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        tv_title.setOnClickListener {
            val dialog = ReNameDeviceFragment()
            val bundle = Bundle()
            bundle.putInt("deviceId", room.id)
            bundle.putString("deviceName", room.name)
            dialog.arguments = bundle
            dialog.setUpdateZoneNameListener(this)
            dialog.show(fragmentManager, "ReNameDeviceFragment")
        }
        tv_title.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (!hasShowRenameRoomGuide) showGuideView(tv_title)
                tv_title.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, position: Int) {
        val singleDevice = adapter.getItem(position)
        if (singleDevice != null) {
            val bundle = Bundle()
            bundle.putInt("updateType", 1)
            mDialog.arguments = bundle
            mDialog.isCancelable = false
            mDialog.show(fragmentManager, "GroupUpdateFragment")
            bindDevice(room.zoneId, room.instructId, singleDevice.instructId, "remove")

        }
        menuBridge?.closeMenu()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    override fun onCheckedChanged(item: SingleDevice, isChecked: Boolean) {
        val controller = ControllerFactory().createController(item.type)
        if (listener.isMeshServiceConnected()) controller?.setLightPowerState(item.instructId, if (isChecked) 1 else 0)
        changeDeviceState(item, "on", if (isChecked) "1" else "0")
    }


    private fun showGuideView(view: View) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingLeft(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingRight(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingBottom(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetPaddingTop(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setOverlayTarget(false)
                .setOutsideTouchable(false)
        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {
                hasShowRenameRoomGuide = true
            }

            override fun onDismiss() {}
        })
        builder.addComponent(object : Component {
            override fun getView(inflater: LayoutInflater): View {
                return inflater.inflate(R.layout.view_guide_device_rename, null)
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
        val guide = builder.createGuide()
        guide?.setShouldCheckLocInWindow(true)
        guide?.show(activity)
    }

    private fun bindDevice(zoneId: Int, groupInstructId: Int, deviceInstructId: Int, act: String) {
        context?.getIMEI()?.let { it1 ->
            viewModel.bindDevice(it1, zoneId, groupInstructId, deviceInstructId, act).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                if (it?.status == Status.SUCCESS) {
                    mDialog.dismiss()
                } else if (it?.status == Status.ERROR) {
                    mDialog.dismiss()
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

    private fun changeDeviceState(singleDevice: SingleDevice, key: String, value: String) {
        context?.getIMEI()?.let { it1 ->
            viewModel.changeDeviceState(it1, singleDevice.id, key, value).observe(viewLifecycleOwner, Observer<Resource<SingleDevice>> {
                if (it?.status == Status.SUCCESS) {

                } else if (it?.status == Status.ERROR) {
//                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

    override fun updateDeviceName(id: Int, newName: String) {
        context?.getIMEI()?.let { it1 ->
            viewModel.changeRoomName(it1, room.zoneId, id, room.type, newName).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                if (it?.status == Status.SUCCESS) {
                    tv_title.text = newName
                } else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }
}

