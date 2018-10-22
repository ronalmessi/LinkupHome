package com.ihomey.linkuphome.main

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ControlDeviceListAdapter
import com.ihomey.linkuphome.adapter.MeshLampPageAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentLampMeshBinding
import com.ihomey.linkuphome.device.DeviceConnectFragment
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.disableShiftMode
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.SpaceItemDecoration

class MeshLampFragment : BaseFragment(), FragmentBackHandler, BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener, BaseQuickAdapter.OnItemClickListener {

    private var categoryType: Int = -1
    private val adapter = ControlDeviceListAdapter(R.layout.control_device_list_item)
    private lateinit var mViewDataBinding: FragmentLampMeshBinding
    private var mViewModel: MainViewModel? = null
    private lateinit var behavior: BottomSheetBehavior<View>

    fun newInstance(categoryType: Int): MeshLampFragment {
        val fragment = MeshLampFragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel?.getControlDeviceResults()?.observe(this, Observer<Resource<List<ControlDevice>>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                val newData = arrayListOf<ControlDevice>()
                newData.addAll(it.data)
                newData.add(ControlDevice(-1, Device(getString(R.string.initialize), -1), ControlState()))
                adapter.setNewData(newData)
            }
        })
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                mViewDataBinding.controlBaseBnv.selectedItemId = R.id.item_tab_mesh_control
            }
        })
        mViewModel?.getBridgeState()?.observe(this, Observer<Boolean> {
            if (it != null && !it) {
                if(activity!=null){
                    activity.onBackPressed()
                    val fsh = activity as IFragmentStackHolder
                    fsh.replaceFragment(R.id.container, DeviceConnectFragment().newInstance(arguments.getInt("categoryType", -1), true, true))
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lamp_mesh, container, false)
        categoryType = arguments.getInt("categoryType", -1)
        mViewDataBinding.controlBaseBnv.disableShiftMode(1.5f)
        mViewDataBinding.controlBaseVp.offscreenPageLimit = 3
        mViewDataBinding.controlBaseVp.adapter = MeshLampPageAdapter(categoryType, childFragmentManager)
        mViewDataBinding.controlBaseVp.addOnPageChangeListener(null)
        mViewDataBinding.controlBaseBnv.setOnNavigationItemSelectedListener(this)
        mViewDataBinding.controlBaseBnv.setOnNavigationItemReselectedListener(this)
        initControlDeviceSelectionDialog()
        return mViewDataBinding.root
    }

    private fun initControlDeviceSelectionDialog() {
        mViewDataBinding.controlDeviceRcvList.setPadding(context.dip2px(8f), context.dip2px(8f), context.dip2px(8f), context.dip2px(8f))
        mViewDataBinding.controlDeviceRcvList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.controlDeviceRcvList.addItemDecoration(SpaceItemDecoration(context.dip2px(2f)))
        mViewDataBinding.controlDeviceRcvList.adapter = adapter
        mViewDataBinding.controlDeviceRcvList.itemAnimator = DefaultItemAnimator()
        behavior = BottomSheetBehavior.from(mViewDataBinding.controlDeviceRcvList)
        behavior.peekHeight = 0
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                mViewDataBinding.blackView.visibility = View.VISIBLE
                mViewDataBinding.blackView.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mViewDataBinding.blackView.visibility = View.GONE
                } else {
                    mViewDataBinding.blackView.visibility = View.VISIBLE
                }
            }
        })
        mViewDataBinding.blackView.setOnClickListener {
            hideControlDeviceSelectionDialog()
        }
        val lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$categoryType", -1)
        if (lastUsedDeviceId == -1) {
            mViewDataBinding.controlBaseBnv.selectedItemId = R.id.item_tab_mesh_device
        }
        adapter.onItemClickListener = this
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_tab_mesh_control -> {
                mViewDataBinding.controlBaseBnv.setBackgroundResource(R.drawable.control_base_bg)
                mViewDataBinding.clLampContainer.background = null
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                mViewDataBinding.controlBaseVp.currentItem = 0
            }
            R.id.item_tab_mesh_device -> {
                mViewDataBinding.controlBaseBnv.background = null
                mViewDataBinding.clLampContainer.setBackgroundResource(R.mipmap.fragment_led_bg)
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                mViewDataBinding.controlBaseVp.currentItem = 1
            }
            R.id.item_tab_mesh_group -> {
                mViewDataBinding.clLampContainer.background = null
                mViewDataBinding.controlBaseBnv.setBackgroundResource(R.drawable.control_base_bg)
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                mViewDataBinding.controlBaseVp.currentItem = 2
            }
            R.id.item_tab_mesh_device_select -> behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return true
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        if (item.itemId == R.id.item_tab_mesh_device_select) {
            hideControlDeviceSelectionDialog()
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        hideControlDeviceSelectionDialog()
        val controlDevice = adapter?.getItem(position) as ControlDevice
        if (controlDevice.id != -1) {
            mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(controlDevice.device.type, controlDevice.id))
        } else {
            var isShare by PreferenceHelper("share_$categoryType", false)
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.device_clear)
            builder.setPositiveButton(R.string.confirm) { _, _ ->
                if (isShare) {
                    isShare = false
                    mViewModel?.removeAllDevices(categoryType)
                } else {
                    mViewModel?.removeAllGroupDevices(categoryType)
                }
            }
            builder.setNegativeButton(R.string.cancel, null)
            builder.setCancelable(false)
            builder.create().show()
        }
    }


    override fun onBackPressed(): Boolean {
        return if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            hideControlDeviceSelectionDialog()
            true
        } else {
            handleBackPress(this)
        }
    }

    private fun hideControlDeviceSelectionDialog() {
        val menuView = mViewDataBinding.controlBaseBnv.getChildAt(0) as BottomNavigationMenuView
        val item = menuView.getChildAt(mViewDataBinding.controlBaseVp.currentItem) as BottomNavigationItemView
        mViewDataBinding.controlBaseBnv.selectedItemId = item.id
    }


}