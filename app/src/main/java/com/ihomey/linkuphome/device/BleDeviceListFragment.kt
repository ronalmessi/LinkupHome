package com.ihomey.linkuphome.device

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentDeviceBleListBinding
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.VerticalSpaceItemDecoration
import com.yanzhenjie.loading.Utils
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class BleDeviceListFragment : BaseFragment(), SwipeItemClickListener, SwipeMenuItemClickListener {

    private var lampCategoryType: Int = -1
    private var isDeviceRemoving = false
    private val keyMap: HashMap<String, String> = HashMap()
    private var setting: LampCategory? = null
    private lateinit var mViewDataBinding: FragmentDeviceBleListBinding
    private var adapter: DeviceListAdapter? = null
    private var mViewModel: MainViewModel? = null
    private val deviceAssociateFragment = BleDeviceAssociateFragment()

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
                if (adapter?.itemCount == 0) adapter?.setNewData(it.data)
                if (adapter?.itemCount == 0) {
                    mViewDataBinding.llBleDeviceSearching.visibility = View.VISIBLE
                } else {
                    mViewDataBinding.llBleDeviceSearching.visibility = View.GONE
                }
                if (!BleManager.getInstance().isBlueEnable) {
                    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, 0x01)
                } else {
                    discoverDevices()
                }
            }
        })

        mViewModel?.getLocalSetting()?.observe(this, Observer<Resource<LampCategory>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                setting = it.data
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BleManager.getInstance().cancelScan()
        keyMap.clear()
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val singleDevice = adapter?.getItem(position)
        if (singleDevice != null) {
            if (!BleManager.getInstance().isConnected(singleDevice.macAddress) && singleDevice.id == 0) {
                connectBleDevice(singleDevice)
            } else {
                mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(singleDevice.device.type, singleDevice.id))
            }
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
                keyMap.remove(singleDevice.macAddress)
                adapter?.remove(position)
                mViewModel?.deleteSingleDevice(lampCategoryType, singleDevice.id)
                val bluetoothDevice = BleManager.getInstance().bluetoothAdapter.getRemoteDevice(singleDevice.macAddress)
                val bleDevice = BleDevice(bluetoothDevice, 0, null, 0)
                BleManager.getInstance().disconnect(bleDevice)
                isDeviceRemoving = false
                BleManager.getInstance().cancelScan()
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
        val scanRuleConfig = BleScanRuleConfig.Builder().setDeviceName(true, "Linkuphome M1").setAutoConnect(false).setScanTimeOut(-1).build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {
            }

            override fun onScanStarted(success: Boolean) {

            }

            override fun onScanning(bleDevice: BleDevice) {
                if (!isDeviceRemoving && !keyMap.containsKey(bleDevice.mac)) {
                    keyMap[bleDevice.mac] = bleDevice.name
                    val device = SingleDevice(0, Device(bleDevice.name, lampCategoryType), 0, bleDevice.mac, 0, 0, 0, null)
                    val position = adapter?.data?.indexOf(device) ?: -1
                    if (bleDevice.mac != null && bleDevice.name != null && position == -1) {
                        mViewDataBinding.llBleDeviceSearching.visibility = View.GONE
                        adapter?.addData(device)
                    }
                }
            }
        })
    }

    private fun connectBleDevice(singleDevice: SingleDevice) {
        BleManager.getInstance().setReConnectCount(0).connect(singleDevice.macAddress, object : BleGattCallback() {
            override fun onStartConnect() {
                deviceAssociateFragment.isCancelable = false
                deviceAssociateFragment.show(activity.fragmentManager, "DeviceAssociateFragment")
                mViewDataBinding.llBleDeviceSearching.postDelayed(associateProgressChangedAction, 300)
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                deviceAssociateFragment.dismiss()
            }

            override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt?, status: Int) {
                mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                val device = SingleDevice(setting?.nextDeviceIndex!!, Device(singleDevice.device.name, lampCategoryType), 0, bleDevice.mac, 0, 0, 0, ControlState())
                val position = adapter?.data?.indexOf(device) ?: -1
                if (position != -1) {
                    adapter?.getItem(position)?.id = setting?.nextDeviceIndex!!
                    adapter?.notifyItemChanged(position)
                    mViewModel?.addSingleDevice(setting!!, device)
                }
                deviceAssociateFragment.dismiss()
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                deviceAssociateFragment.dismiss()
                activity.toast(exception.toString())
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