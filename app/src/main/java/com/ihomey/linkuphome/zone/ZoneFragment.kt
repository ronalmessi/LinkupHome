package com.ihomey.linkuphome.zone

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import cn.iclass.guideview.GuideBuilder
import com.chad.library.adapter.base.BaseQuickAdapter
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
import kotlinx.android.synthetic.main.view_zone_list_empty.*
import kotlinx.android.synthetic.main.zone_fragment.*

class ZoneFragment : BaseFragment(), FragmentBackHandler,  DeleteSubZoneListener,   FragmentVisibleStateListener, RoomListAdapter.OnItemClickListener, RoomListAdapter.OnItemChildClickListener, RoomListAdapter.OnCheckedChangeListener, RoomListAdapter.OnSeekBarChangeListener {

    companion object {
        fun newInstance() = ZoneFragment()
    }

    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var adapter: RoomListAdapter
    private lateinit var meshServiceStateListener: MeshServiceStateListener
    private var guide: Guide? = null
    private var isFragmentVisible = false

    private var isUserTouch: Boolean=false
    private var roomList:List<RoomAndDevices>?=null

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
        mViewModel.roomsResult.observe(this, Observer<PagedList<RoomAndDevices>> {
            roomList=it
            Log.d("aa","roomsResult--"+isUserTouch)
            if(!isUserTouch)adapter.submitList(it)
        })

        mViewModel.isRoomListEmptyLiveData.observe(viewLifecycleOwner, Observer<Boolean> {
            if(it){
                emptyView.visibility=View.VISIBLE
                iv_add.visibility = View.INVISIBLE
            } else{
                emptyView.visibility=View.GONE
                iv_add.visibility = View.VISIBLE
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as ZoneNavHostFragment).showBottomNavigationBar(true) }
        adapter = RoomListAdapter()
        adapter.setOnItemClickListener(this)
        adapter.setOnItemChildClickListener(this)
        adapter.setOnCheckedChangeListener(this)
        adapter.setOnSeekBarChangeListener(this)
        rcv_zone_list.layoutManager = LinearLayoutManager(context)
        (rcv_zone_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        context?.resources?.getDimension(R.dimen._12sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_zone_list.addItemDecoration(it) }
        rcv_zone_list.adapter = adapter
        btn_create_zone.setOnClickListener {
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
        if(!isVisible) isUserTouch=false
        Log.d("aa","ZoneFragment---"+isFragmentVisible)
        if (!hasShowBindDeviceGuide && isVisible&&adapter.itemCount > 0) {
            adapter.currentList?.get(0).let {
                rcv_zone_list.post{ rcv_zone_list.layoutManager?.findViewByPosition(0)?.let { showGuideView(it,adapter.itemCount) } }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        rcv_zone_list.postDelayed({
            if (!hasShowBindDeviceGuide&&isFragmentVisible&& adapter.itemCount > 0) {
                adapter.currentList?.get(0).let {
                    rcv_zone_list.post{ rcv_zone_list.layoutManager?.findViewByPosition(0)?.let { showGuideView(it,adapter.itemCount) } }
                }
            }
        }, 250)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isUserTouch=false
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

    override fun onItemClick(position: Int) {
        hideGuideView()
       roomList?.get(position)?.let {
           mViewModel.setSelectedRoom(it)
           NavHostFragment.findNavController(this@ZoneFragment).navigate(R.id.action_tab_zones_to_subZoneFragment)
       }
    }

    override fun onItemChildClick(position: Int, view: View) {
        roomList?.get(position)?.let {
            when (view.id) {
                R.id.btn_delete -> {
                    val room=it.room
                    if(room!=null){
                        val dialog = DeleteRoomFragment()
                        val bundle = Bundle()
                        bundle.putInt("zoneId", room.id)
                        dialog.arguments = bundle
                        dialog.setDeleteSubZoneListener(this)
                        dialog.show(fragmentManager, "DeleteRoomFragment")
                    }
                }

                R.id.iv_color_cycling -> {
                    val dialog = ColorCyclingSettingFragment()
                    mViewModel.setSelectedRoom(it)
                    dialog.show(fragmentManager, "ColorCyclingSettingFragment")
                }

                R.id.iv_lighting -> {
                    if (isFragmentVisible) {
                        for (index in it.devices.indices) {
                            val device = it.devices[index]
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

    override fun onProgressChanged(position: Int, progress: Int) {
       if(isFragmentVisible){
           isUserTouch=true
           roomList?.get(position)?.let {
               for (index in it.devices.indices) {
                   val device = it.devices[index]
                   val controller = ControllerFactory().createController(device.type)
                   if (meshServiceStateListener.isMeshServiceConnected()) {
                       Handler().postDelayed({ controller?.setLightBright(device.instructId, progress.plus(15)) }, 100L * index)
                   }
               }
               it.room?.let { changeRoomState(it, "brightness", progress.toString()) }
           }
       }
    }

    override fun onCheckedChanged(position: Int, isChecked: Boolean) {
        if(isFragmentVisible){
            isUserTouch=true
            roomList?.get(position)?.let {
                for (index in it.devices.indices) {
                    val device = it.devices[index]
                    val controller = ControllerFactory().createController(device.type)
                    if (meshServiceStateListener.isMeshServiceConnected()) {
                        Handler().postDelayed({ controller?.setLightPowerState(device.instructId, if (isChecked) 1 else 0) }, 100L * index)
                    }
                }
                it.room?.let { changeRoomState(it, "on", if (isChecked) "1" else "0") }
            }
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

    private fun showGuideView(view: View, size: Int) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(context?.resources?.getDimension(R.dimen._6sdp)?.toInt()!!)
                .setHighTargetMarginTop(getMarginTop(rcv_zone_list) + if (size > 0) context?.resources?.getDimension(R.dimen._10sdp)?.toInt()!! else context?.resources?.getDimension(R.dimen._12sdp)?.toInt()!!)
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
        if (guide == null) {
            guide = builder.createGuide()
            guide?.setShouldCheckLocInWindow(true)
        }
        if (!guide?.isVisible!!) guide?.show(context as Activity)
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
            })
        }
    }

}
