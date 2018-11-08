package com.ihomey.linkuphome.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.LocalBroadcastManager
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
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.ihomey.linkuphome.HexUtil
import com.ihomey.linkuphome.adapter.DrawerMenuAdapter
import com.ihomey.linkuphome.base.LocaleHelper.getLanguage
import com.ihomey.linkuphome.controller.BedController
import com.ihomey.linkuphome.data.vo.*

import com.ihomey.linkuphome.widget.toprightmenu.MenuItem
import com.ihomey.linkuphome.listener.OnDrawerMenuItemClickListener
import com.ihomey.linkuphome.listener.SensorValueListener
import com.ihomey.linkuphome.widget.DividerDecoration


class BleLampFragment : BaseFragment(), FragmentBackHandler, BottomNavigationView.OnNavigationItemSelectedListener, BaseQuickAdapter.OnItemClickListener {

    private var categoryType: Int = -1
    private lateinit var mViewDataBinding: FragmentLampBleBinding
    private var mViewModel: MainViewModel? = null
    private var onDrawerMenuItemClickListener: OnDrawerMenuItemClickListener? = null
    private var isReName: Boolean = false
    private var sensorType: String = "F1"
    private lateinit var drawerMenuAdapter: DrawerMenuAdapter

    fun newInstance(categoryType: Int): BleLampFragment {
        val fragment = BleLampFragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerSensorValueReceiver()
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                Log.d("aa", "getCurrentControlDevice--" + it.data.id + "--" + it.data.device.macAddress)
                if (!isReName()) mViewDataBinding.controlBaseBnv.selectedItemId = R.id.item_tab_ble_control
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
        menuItems.add(MenuItem(R.drawable.ic_menu_temperature, R.string.drawer_menu_temperature))
        menuItems.add(MenuItem(R.drawable.ic_menu_humidity, R.string.drawer_menu_humidity))
        drawerMenuRcv?.layoutManager = LinearLayoutManager(context)
        drawerMenuAdapter = DrawerMenuAdapter(R.layout.item_drawer_menu, menuItems)
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


    private fun registerSensorValueReceiver() {
        val lbm = LocalBroadcastManager.getInstance(context)
        val filter = IntentFilter()
        filter.addAction("com.ihomey.linkuphome.SENSOR_VALUE_CHANGED")
        lbm.registerReceiver(sensorValueReceiver, filter)
    }

    private val sensorValueReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sensorValue = intent.getStringExtra("sensorValue")
            if (Integer.parseInt(sensorValue.substring(16, 18).toUpperCase(), 16) > 240) {
                val sensorTypeValue = sensorValue.substring(16, 18).toUpperCase()
                setSensorType(sensorTypeValue)
            }
        }
    }

    private fun setSensorType(sensorType: String) {
        this.sensorType = sensorType
        val menuItems = ArrayList<MenuItem>()
        if (TextUtils.equals(getLanguage(context), "F1")) {
            menuItems.add(MenuItem(R.drawable.ic_menu_temperature, R.string.drawer_menu_temperature))
            menuItems.add(MenuItem(R.drawable.ic_menu_humidity, R.string.drawer_menu_humidity))
        } else {
            menuItems.add(MenuItem(R.drawable.ic_menu_temperature, R.string.drawer_menu_temperature))
//            menuItems.add(MenuItem(R.drawable.ic_menu_humidity, R.string.drawer_menu_humidity))
            menuItems.add(MenuItem(R.drawable.ic_menu_pm25, R.string.drawer_menu_pm25))
            menuItems.add(MenuItem(R.drawable.ic_menu_hcho, R.string.drawer_menu_hcho))
            menuItems.add(MenuItem(R.drawable.ic_menu_voc, R.string.drawer_menu_voc))
        }
        drawerMenuAdapter.setNewData(menuItems)
    }

    override fun onBackPressed(): Boolean {
        return handleBackPress(this)
    }


    override fun onStop() {
        super.onStop()
        BleManager.getInstance().cancelScan()
        BleManager.getInstance().disconnectAllDevice()
        BleManager.getInstance().destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(sensorValueReceiver)
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

    private fun isReName(): Boolean {
        return isReName
    }

    fun setIsReName(flag: Boolean) {
        this.isReName = flag
    }

    fun getSensorType(): String {
        return sensorType
    }
}