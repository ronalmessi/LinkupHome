package com.ihomey.linkuphome.device

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.BluetoothClientManager
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentDeviceBleListBinding
import com.ihomey.linkuphome.disconnect
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.VerticalSpaceItemDecoration
import com.inuker.bluetooth.library.Code.REQUEST_SUCCESS
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.options.BleConnectOptions
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.yanzhenjie.loading.Utils
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class BleDeviceListFragment : BaseFragment(), SwipeItemClickListener, SwipeMenuItemClickListener {

    private var lampCategoryType: Int = -1
    private var isDeviceRemoving = false
    private var setting: LampCategory? = null
    private lateinit var mViewDataBinding: FragmentDeviceBleListBinding
    private var adapter: DeviceListAdapter? = null
    private var mViewModel: MainViewModel? = null
    private val deviceAssociateFragment = BleDeviceAssociateFragment()

    private val mClient = BluetoothClientManager.getInstance().client

    fun newInstance(lampCategoryType: Int): BleDeviceListFragment {
        val fragment = BleDeviceListFragment()
        val bundle = Bundle()
        bundle.putInt("lampCategoryType", lampCategoryType)
        fragment.arguments = bundle
        return fragment
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_ble_list, container, false)
        mViewDataBinding.toolbarBack.setOnClickListener { activity.onBackPressed() }
        lampCategoryType = arguments.getInt("lampCategoryType", -1)
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DeviceListAdapter(R.layout.lamp_device_mesh_list_item)

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = Utils.dip2px(context, 48f)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }

        mViewDataBinding.lampDeviceBleRcvList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.lampDeviceBleRcvList.addItemDecoration(VerticalSpaceItemDecoration(Utils.dip2px(context, 12f).toInt()))
        mViewDataBinding.lampDeviceBleRcvList.adapter = adapter
        mViewDataBinding.lampDeviceBleRcvList.setSwipeItemClickListener(this)
        mViewDataBinding.lampDeviceBleRcvList.setSwipeMenuCreator(swipeMenuCreator)
        mViewDataBinding.lampDeviceBleRcvList.setSwipeMenuItemClickListener(this)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel?.getDeviceResults()?.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                var hasConnected by PreferenceHelper("hasConnected$lampCategoryType", false)
                hasConnected = !(it.data == null || it.data.isEmpty())
                if (adapter?.itemCount == 0) {
                    adapter?.setNewData(it.data)
                    if (it.data != null) connectBleDevices(it.data)
                }
                if (adapter?.itemCount == 0) {
                    mViewDataBinding.llBleDeviceSearching.visibility = View.VISIBLE
                } else {
                    mViewDataBinding.llBleDeviceSearching.visibility = View.GONE
                }
                discoverDevices()
            }
        })

        mViewModel?.getLocalSetting()?.observe(this, Observer<Resource<LampCategory>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                setting = it.data
            }
        })
    }

    private fun connectBleDevices(data: List<SingleDevice>) {
        for (device in data) {
            connectBleDevice(device, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BleManager.getInstance().cancelScan()
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val singleDevice = adapter?.getItem(position)
        if (singleDevice != null) {
            if (!BleManager.getInstance().isConnected(singleDevice.device.macAddress)) {
//                BleManager.getInstance().cancelScan()
                connectBleDevice(singleDevice, false)
            } else {
                mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(singleDevice.device.type, singleDevice.id))
            }

//            val status = mClient.getConnectStatus(singleDevice.device.macAddress)
//            if (status != Constants.STATUS_DEVICE_CONNECTED && singleDevice.id == 0) {
////                connectBleDevice(singleDevice, false)
//            } else {
//                mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(singleDevice.device.type, singleDevice.id))
//            }
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge) {
        val singleDevice = adapter?.getItem(menuBridge.adapterPosition)
        if (singleDevice?.id != 0) {
            isDeviceRemoving = true
            showDeviceRemoveAlertDialog(singleDevice!!)
        }
        menuBridge.closeMenu()
    }

    private fun showDeviceRemoveAlertDialog(singleDevice: SingleDevice) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(getString(R.string.delete) + " " + singleDevice.device.name + "?")
        builder.setPositiveButton(R.string.confirm) { _, _ ->
            val position = adapter?.data?.indexOf(singleDevice) ?: -1
            if (position != -1) {
                adapter?.remove(position)
                mViewModel?.deleteSingleDevice(lampCategoryType, singleDevice.id)
                isDeviceRemoving = false
                BleManager.getInstance().disconnect(singleDevice.device.macAddress)
                mViewDataBinding.lampDeviceBleRcvList.postDelayed({ discoverDevices() }, 1000)

            }
        }
        builder.setNegativeButton(R.string.cancel, null)
        builder.setCancelable(false)
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0x01) {
            discoverDevices()
        }
    }

    private fun discoverDevices() {
//        val request = SearchRequest.Builder().searchBluetoothLeDevice(1000 * 5, 1).build()
//        mClient.search(request, object : SearchResponse {
//            override fun onSearchStopped() {
//                discoverDevices()
////                Log.d("aa", "onSearchStopped")
//            }
//
//            override fun onSearchStarted() {
////                Log.d("aa", "onSearchStarted")
//            }
//
//            override fun onSearchCanceled() {
////                Log.d("aa", "onSearchCanceled")
//            }
//
//            override fun onDeviceFounded(bleDevice: SearchResult) {
//                if (!isDeviceRemoving && TextUtils.equals("Linkuphome M1", bleDevice.name) && !keyMap.containsKey(bleDevice.address)) {
//                    keyMap[bleDevice.address] = bleDevice.name
//                    val device = SingleDevice(0, Device(bleDevice.name, lampCategoryType,bleDevice.address), 0,  0, 0, 0, null)
//                    val position = adapter?.data?.indexOf(device) ?: -1
//                    if (bleDevice.address != null && bleDevice.name != null && position == -1) {
//                        mViewDataBinding.llBleDeviceSearching.visibility = View.GONE
//                        adapter?.addData(device)
//                    }
//                }
//            }
//        })
        val scanRuleConfig = BleScanRuleConfig.Builder()
                .setServiceUuids(null)
                .setDeviceName(true, "Linkuphome M1")
                .setDeviceMac(null)
                .setAutoConnect(false)
                .setScanTimeOut(10000)
                .build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
                Log.d("aa", "size--" + scanResultList?.size)
            }

            override fun onScanStarted(success: Boolean) {

            }

            override fun onScanning(bleDevice: BleDevice?) {
                if (!isDeviceRemoving && bleDevice?.mac != null && bleDevice.name != null) {
                    val device = SingleDevice(0, Device(bleDevice.name, lampCategoryType, bleDevice.mac), 0, 0, 0, 0, null)
                    val position = adapter?.data?.indexOf(device) ?: -1
                    if (position == -1) {
                        mViewDataBinding.llBleDeviceSearching.visibility = View.GONE
                        adapter?.addData(device)
                    }
                }
            }
        })
    }

    private fun connectBleDevice(singleDevice: SingleDevice, isAutoConnect: Boolean) {
        if (!isAutoConnect) {
            deviceAssociateFragment.isCancelable = false
            deviceAssociateFragment.show(activity.fragmentManager, "DeviceAssociateFragment")
            mViewDataBinding.llBleDeviceSearching.postDelayed(associateProgressChangedAction, 300)
        }
//        val options = BleConnectOptions.Builder()
//                .setConnectRetry(10)   // 连接如果失败重试3次
//                .setConnectTimeout(30000)   // 连接超时30s
//                .setServiceDiscoverRetry(10)  // 发现服务如果失败重试3次
//                .setServiceDiscoverTimeout(30000)  // 发现服务超时20s
//                .build()
//        mClient.connect(singleDevice.device.macAddress, if (isAutoConnect) options else null) { code, data ->
//            if (!isAutoConnect) {
//                if (code == REQUEST_SUCCESS) {
//                    val device = SingleDevice(setting?.nextDeviceIndex!!, Device(singleDevice.device.name, lampCategoryType, singleDevice.device.macAddress), 0, 0, 0, 0, ControlState())
//                    val position = adapter?.data?.indexOf(device) ?: -1
//                    if (position != -1) {
//                        adapter?.getItem(position)?.id = setting?.nextDeviceIndex!!
//                        adapter?.notifyItemChanged(position)
//                        mViewModel?.addSingleDevice(setting!!, device)
//                    }
//                }
//                mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
//                deviceAssociateFragment.dismiss()
//            } else {
//
//            }
//            Log.d("aa", "code-33333-" + code)
//        }
        BleManager.getInstance().connect(singleDevice.device.macAddress, object : BleGattCallback() {
            override fun onStartConnect() {
                Log.d("aa", "11")
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                Log.d("aa", "22" + isActiveDisConnected)
                if (!isAutoConnect) {
                    mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                    deviceAssociateFragment.dismiss()
                }
            }

            override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                Log.d("aa", "333")
                if (!isAutoConnect) {
                    val device = SingleDevice(setting?.nextDeviceIndex!!, Device(singleDevice.device.name, lampCategoryType, singleDevice.device.macAddress), 0, 0, 0, 0, ControlState())
                    val position = adapter?.data?.indexOf(device) ?: -1
                    if (position != -1) {
                        adapter?.getItem(position)?.id = setting?.nextDeviceIndex!!
                        adapter?.notifyItemChanged(position)
                        mViewModel?.addSingleDevice(setting!!, device)
                    }
                    mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                    deviceAssociateFragment.dismiss()
                }
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                Log.d("aa", "444")
                if (!isAutoConnect) {
                    mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                    deviceAssociateFragment.dismiss()
                    activity.toast(getString(R.string.device_associate_fail))
                }
            }
        })
    }

    private val associateProgressChangedAction = object : Runnable {
        override fun run() {
            val progress = deviceAssociateFragment.getProgress()
            if (progress < 90) {
                deviceAssociateFragment.onAssociateProgressChanged(progress + 15)
                mViewDataBinding.llBleDeviceSearching.postDelayed(this, 300)
            }
        }
    }

}