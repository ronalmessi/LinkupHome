package com.ihomey.linkuphome.room

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import cn.iclass.guideview.GuideBuilder
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.BondedDeviceListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.RoomAndDevices
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.dialog.InputDialogFragment
import com.ihomey.linkuphome.getDeviceId
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.InputDialogInterface
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuBridge
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import kotlinx.android.synthetic.main.room_fragment.*
import kotlinx.android.synthetic.main.view_device_list_empty.*


class RoomFragment : BaseFragment(), FragmentBackHandler, OnItemMenuClickListener, BondedDeviceListAdapter.OnCheckedChangeListener, InputDialogInterface {

    companion object {
        fun newInstance() = RoomFragment()
    }

    var hasShowRenameRoomGuide by PreferenceHelper("hasShowRenameRoomGuide", false)

    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var mViewModel: RoomViewModel
    private lateinit var adapter: BondedDeviceListAdapter

    private var room: Room? = null

    private var guide: Guide? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.room_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(RoomViewModel::class.java)
        viewModel.mSelectedRoom.observe(viewLifecycleOwner, Observer<RoomAndDevices> {
            tv_title.text = it.room?.name
            room = it.room
            mViewModel.setCurrentRoom(room)
        })
        mViewModel.bondedDevicesResult1.observe(viewLifecycleOwner, Observer<PagedList<Device>> {
            adapter.submitList(it)
        })
        mViewModel.isBondedDevicesListEmptyLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) {
                emptyView.visibility = View.VISIBLE
                btn_add.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                btn_add.visibility = View.VISIBLE
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as BaseNavHostFragment).showBottomNavigationBar(false) }
        adapter = BondedDeviceListAdapter()
        adapter.setOnCheckedChangeListener(this)
        rcv_device_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_device_list.addItemDecoration(it) }
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context?.resources?.getDimension(R.dimen._72sdp)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width!!.toInt()).setHeight(height).setText(R.string.action_delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }
        rcv_device_list.setSwipeMenuCreator(swipeMenuCreator)
        rcv_device_list.setOnItemMenuClickListener(this)
        rcv_device_list.adapter = adapter
        btn_add_device.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_subZoneFragment_to_unBondedDevicesFragment2)
        }
        btn_add.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_subZoneFragment_to_unBondedDevicesFragment2)
        }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        tv_title.setOnClickListener {
            room?.let {
                hideGuideView()
                val dialog = InputDialogFragment()
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.title_rename))
                bundle.putString("inputText", it.name)
                dialog.arguments = bundle
                dialog.setInputDialogInterface(this)
                dialog.show(fragmentManager, "InputDialogFragment")
            }
        }
        tv_title.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (!hasShowRenameRoomGuide) showGuideView(tv_title)
                tv_title.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?, position: Int) {
        adapter.currentList?.get(position)?.let { it1 ->
            room?.let { bindDevice(it.zoneId, it.id, it1.id, "remove") }
        }
        menuBridge?.closeMenu()
    }


    override fun onCheckedChanged(position: Int, isChecked: Boolean) {
        adapter.currentList?.get(position)?.let {
            LightControllerFactory().createCommonController(it)?.setOnOff(isChecked)
            changeDeviceState(it, "on", if (isChecked) "1" else "0")
        }
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
                .setHighTargetMarginTop(getMarginTop(view) + context?.resources?.getDimension(R.dimen._13sdp)?.toInt()!!)
                .setAutoDismiss(true)
                .setOverlayTarget(false)
                .setOutsideTouchable(true)
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
        guide = builder.createGuide()
        guide?.setShouldCheckLocInWindow(true)
        guide?.show(activity)
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

    private fun bindDevice(zoneId: Int, groupId: Int, deviceId: String, act: String) {
        context?.getDeviceId()?.let { it1 ->
            viewModel.bindDevice(it1, zoneId, groupId, deviceId, act).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                mViewModel.setCurrentRoom(room)
                if (it?.status == Status.SUCCESS) {
                    hideLoadingView()
                } else if (it?.status == Status.ERROR) {
                    hideLoadingView()
                    it.message?.let { it2 -> activity?.toast(it2) }
                } else if (it?.status == Status.LOADING) {
                    showLoadingView()
                    it.message?.let { it2 -> activity?.toast(it2) }
                }
            })
        }
    }

    private fun changeDeviceState(device: Device, key: String, value: String) {
        updateState(device, key, value)
        context?.getDeviceId()?.let { it1 ->
            viewModel.changeDeviceState(it1, device.id, key, value).observe(viewLifecycleOwner, Observer<Resource<Device>> {

            })
        }
    }

    override fun onBackPressed(): Boolean {
        return if (guide != null && guide?.isVisible!!) {
            hideGuideView()
            true
        } else {
            false
        }
    }

    override fun onInput(text: String) {
        context?.getDeviceId()?.let { it1 ->
            room?.let {
                viewModel.changeRoomName(it1, it.zoneId, it.id, it.type, text).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                    if (it?.status == Status.SUCCESS) {
                        hideLoadingView()
                        tv_title.text = text
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

    private fun updateState(device: Device, key: String, value: String) {
        if (TextUtils.equals("brightness", key)) {
            val deviceState = device.parameters
            deviceState?.let {
                it.brightness = value.toInt()
                viewModel.updateDeviceState(device, it)
            }
        } else {
            val deviceState = device.parameters
            deviceState?.let {
                it.on = value.toInt()
                viewModel.updateRoomAndDeviceState(device, it)
            }
        }
    }
}

