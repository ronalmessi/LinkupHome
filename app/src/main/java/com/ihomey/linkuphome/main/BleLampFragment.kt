package com.ihomey.linkuphome.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothGatt
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.clj.fastble.BleManager
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.BleLampPageAdapter
import com.ihomey.linkuphome.databinding.FragmentLampBleBinding
import com.ihomey.linkuphome.disableShiftMode
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.viewmodel.MainViewModel
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log

import android.view.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.clj.fastble.callback.BleScanAndConnectCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.ihomey.linkuphome.adapter.DrawerMenuAdapter
import com.ihomey.linkuphome.base.LocaleHelper.getLanguage
import com.ihomey.linkuphome.data.vo.*

import com.ihomey.linkuphome.widget.toprightmenu.MenuItem
import com.ihomey.linkuphome.listener.OnDrawerMenuItemClickListener
import com.ihomey.linkuphome.widget.DividerDecoration


class BleLampFragment : BaseFragment(), FragmentBackHandler, BottomNavigationView.OnNavigationItemSelectedListener, BaseQuickAdapter.OnItemClickListener {

    private var categoryType: Int = -1
    private lateinit var mViewDataBinding: FragmentLampBleBinding
    private var mViewModel: MainViewModel? = null
    private var onDrawerMenuItemClickListener: OnDrawerMenuItemClickListener? = null

    fun newInstance(categoryType: Int): BleLampFragment {
        val fragment = BleLampFragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                mViewDataBinding.controlBaseBnv.selectedItemId = R.id.item_tab_ble_control
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lamp_ble, container, false)
        categoryType = arguments.getInt("categoryType", -1)
        mViewDataBinding.controlBaseBnv.disableShiftMode(1.2f)
        mViewDataBinding.controlBaseVp.offscreenPageLimit = 4
        mViewDataBinding.controlBaseVp.adapter = BleLampPageAdapter(categoryType, childFragmentManager)
        mViewDataBinding.controlBaseVp.addOnPageChangeListener(null)
        mViewDataBinding.controlBaseBnv.setOnNavigationItemSelectedListener(this)
        mViewDataBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        val drawerMenuRcv = mViewDataBinding.navDrawerLayout?.findViewById<RecyclerView>(R.id.drawer_rcv_menu)
        val menuItems = ArrayList<MenuItem>()
        if (TextUtils.equals(getLanguage(context), "zh")) {
            menuItems.add(MenuItem(R.drawable.ic_menu_temperature, R.string.drawer_menu_temperature))
            menuItems.add(MenuItem(R.drawable.ic_menu_humidity, R.string.drawer_menu_humidity))
            menuItems.add(MenuItem(R.drawable.ic_menu_pm25, R.string.drawer_menu_pm25))
            menuItems.add(MenuItem(R.drawable.ic_menu_hcho, R.string.drawer_menu_hcho))
            menuItems.add(MenuItem(R.drawable.ic_menu_voc, R.string.drawer_menu_voc))
        } else {
            menuItems.add(MenuItem(R.drawable.ic_menu_temperature, R.string.drawer_menu_temperature))
            menuItems.add(MenuItem(R.drawable.ic_menu_humidity, R.string.drawer_menu_humidity))
            menuItems.add(MenuItem(R.drawable.ic_menu_co, R.string.drawer_menu_co))
            menuItems.add(MenuItem(R.drawable.ic_menu_co2, R.string.drawer_menu_co2))
            menuItems.add(MenuItem(R.drawable.ic_menu_voc, R.string.drawer_menu_voc))
        }
        drawerMenuRcv?.layoutManager = LinearLayoutManager(context)
        val drawerMenuAdapter = DrawerMenuAdapter(R.layout.item_drawer_menu, menuItems)
        drawerMenuAdapter.onItemClickListener = this
        drawerMenuRcv?.adapter = drawerMenuAdapter
        drawerMenuRcv?.addItemDecoration(DividerDecoration(context, LinearLayoutManager.VERTICAL, false))

        return mViewDataBinding.root
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_tab_ble_device -> {
                mViewDataBinding.controlBaseVp.currentItem = 0
                mViewDataBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            R.id.item_tab_ble_control -> {
                mViewDataBinding.controlBaseVp.currentItem = 1
                mViewDataBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            R.id.item_tab_environmental_monitor -> {
                mViewDataBinding.controlBaseVp.currentItem = 2
                mViewDataBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            R.id.item_tab_alarm_setting -> {
                mViewDataBinding.controlBaseVp.currentItem = 3
                mViewDataBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        return true
    }


    override fun onResume() {
        super.onResume()
//        val scanRuleConfig = BleScanRuleConfig.Builder()
//                .setDeviceName(true, "Linkuphome M1").setAutoConnect(true).setScanTimeOut(-1).build()
//        BleManager.getInstance().initScanRule(scanRuleConfig)
//        BleManager.getInstance().scanAndConnect(object : BleScanAndConnectCallback() {
//            override fun onStartConnect() {
//
//            }
//
//            override fun onScanStarted(success: Boolean) {
//
//            }
//
//            override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
//
//            }
//
//            override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
//               Log.d("aa",bleDevice?.mac+"---"+BleManager.getInstance())
//            }
//
//            override fun onScanFinished(scanResult: BleDevice?) {
//
//            }
//
//            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
//            }
//
//            override fun onScanning(bleDevice: BleDevice?) {
//
//            }
//
//        })
    }

    override fun onBackPressed(): Boolean {
        return handleBackPress(this)
    }


    override fun onStop() {
        super.onStop()
        BleManager.getInstance().cancelScan()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        if (onDrawerMenuItemClickListener != null) {
            mViewDataBinding.drawerLayout.closeDrawer(Gravity.LEFT)
            val menuItem = (adapter as DrawerMenuAdapter).getItem(position)
            onDrawerMenuItemClickListener?.onMenuItemClick(menuItem!!.id, position)
        }
    }

    fun hideBottomNavigationView() {
        if (mViewDataBinding.controlBaseBnv.visibility == View.VISIBLE) mViewDataBinding.controlBaseBnv.visibility = View.GONE
    }

    fun showBottomNavigationView() {
        if (mViewDataBinding.controlBaseBnv.visibility == View.GONE) mViewDataBinding.controlBaseBnv.visibility = View.VISIBLE
    }

    fun openDrawer() {
        mViewDataBinding.drawerLayout.openDrawer(Gravity.START)
    }

    fun setOnDrawerMenuItemClickListener(onDrawerMenuItemClickListener: OnDrawerMenuItemClickListener) {
        this.onDrawerMenuItemClickListener = onDrawerMenuItemClickListener
    }
}