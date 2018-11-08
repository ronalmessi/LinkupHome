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
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.controller.BedController
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentDeviceBleListBinding
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.VerticalSpaceItemDecoration
import com.yanzhenjie.loading.Utils
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class BleDeviceListFragment : BaseFragment(), SwipeItemClickListener, SwipeMenuItemClickListener, BaseQuickAdapter.OnItemLongClickListener {

    private var lampCategoryType: Int = -1
    private var isDeviceRemoving = false
    private var setting: LampCategory? = null
    private lateinit var mViewDataBinding: FragmentDeviceBleListBinding
    private var deviceListAdapter: DeviceListAdapter? = null
    private var mViewModel: MainViewModel? = null
    private val deviceAssociateFragment = BleDeviceAssociateFragment()

    private val controller: BedController = BedController()

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

        deviceListAdapter = DeviceListAdapter(R.layout.lamp_device_mesh_list_item)
        deviceListAdapter?.onItemLongClickListener = this

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = Utils.dip2px(context, 48f)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }

        mViewDataBinding.lampDeviceBleRcvList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.lampDeviceBleRcvList.addItemDecoration(VerticalSpaceItemDecoration(Utils.dip2px(context, 12f).toInt()))
        mViewDataBinding.lampDeviceBleRcvList.adapter = deviceListAdapter

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
                if (deviceListAdapter?.itemCount == 0) {
                    deviceListAdapter?.setNewData(it.data)
                    if (it.data != null) connectBleDevices(it.data)
                }
                if (deviceListAdapter?.itemCount == 0) {
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

    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        val singleDevice = deviceListAdapter?.getItem(position)
        if (singleDevice != null) {
            val bleLampFragment = parentFragment as BleLampFragment
            bleLampFragment.setIsReName(true)
            val dialog = DeviceRenameFragment()
            val bundle = Bundle()
            bundle.putString("controlDeviceName", singleDevice.device.name)
            bundle.putInt("controlDeviceId", singleDevice.id)
            bundle.putInt("controlDeviceType", 5)
            dialog.arguments = bundle
            dialog.setDeviceRenameListener(object : DeviceRenameFragment.DeviceRenameListener {
                override fun onDeviceNameUpdated(newName: String) {
                    singleDevice.device.name = newName
                    deviceListAdapter?.notifyItemChanged(position)
                }
            })
            dialog.show(activity.fragmentManager, "DeviceRenameFragment")
        }
        return true
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val singleDevice = deviceListAdapter?.getItem(position)
        if (singleDevice != null) {
            if (!BleManager.getInstance().isConnected(singleDevice.device.macAddress)) {
                connectBleDevice(singleDevice, false)
            } else {
                val bluetoothDevice = BleManager.getInstance().bluetoothAdapter.getRemoteDevice(singleDevice.device.macAddress)
                val bleDevice = BleDevice(bluetoothDevice, 0, null, 0)
                BleManager.getInstance().notify(bleDevice, BedController.UUID_SERVICE, BedController.UUID_CHARACTERISTIC_READ, object : BleNotifyCallback() {
                    override fun onCharacteristicChanged(data: ByteArray?) {
                        sendBroadcast(HexUtil.formatHexString(data))
                    }

                    override fun onNotifyFailure(exception: BleException?) {
                        Log.d("aa", exception.toString())
                    }

                    override fun onNotifySuccess() {
                        Log.d("aa", "2222")
                        syncDeviceTime(singleDevice.device.macAddress)
                        controller.getSensorVersion(singleDevice.device.macAddress)
                        val bleLampFragment = parentFragment as BleLampFragment
                        bleLampFragment.setIsReName(false)
                        mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(singleDevice.device.type, singleDevice.id))
                    }
                })
            }
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge) {
        val singleDevice = deviceListAdapter?.getItem(menuBridge.adapterPosition)
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
            val position = deviceListAdapter?.data?.indexOf(singleDevice) ?: -1
            if (position != -1) {
                deviceListAdapter?.remove(position)
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
        val scanRuleConfig = BleScanRuleConfig.Builder().setServiceUuids(null).setDeviceName(true, "Linkuphome M1").setDeviceMac(null).setAutoConnect(false).setScanTimeOut(10000).build()
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
                    val position = deviceListAdapter?.data?.indexOf(device) ?: -1
                    if (position == -1) {
                        mViewDataBinding.llBleDeviceSearching.visibility = View.GONE
                        deviceListAdapter?.addData(device)
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
        BleManager.getInstance().connect(singleDevice.device.macAddress, object : BleGattCallback() {
            override fun onStartConnect() {
                Log.d("aa", "11")
            }

            override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                if (!isAutoConnect) {
                    mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                    deviceAssociateFragment.dismiss()
                }
            }

            override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                Log.d("aa", "333")
                if (!isAutoConnect) {
                    val device = SingleDevice(setting?.nextDeviceIndex!!, Device(singleDevice.device.name, lampCategoryType, singleDevice.device.macAddress), 0, 0, 0, 0, ControlState())
                    val position = deviceListAdapter?.data?.indexOf(device) ?: -1
                    if (position != -1) {
                        deviceListAdapter?.getItem(position)?.id = setting?.nextDeviceIndex!!
                        deviceListAdapter?.notifyItemChanged(position)
                        mViewModel?.addSingleDevice(setting!!, device)
                    }
                    mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                    deviceAssociateFragment.dismiss()
                }

                val lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_5", -1)
                if (singleDevice.id == lastUsedDeviceId) enableNotify(singleDevice.device.macAddress)
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

    private fun enableNotify(mac: String) {
        val bluetoothDevice = BleManager.getInstance().bluetoothAdapter.getRemoteDevice(mac)
        val bleDevice = BleDevice(bluetoothDevice, 0, null, 0)
        BleManager.getInstance().notify(bleDevice, BedController.UUID_SERVICE, BedController.UUID_CHARACTERISTIC_READ, object : BleNotifyCallback() {
            override fun onCharacteristicChanged(data: ByteArray?) {
                sendBroadcast(HexUtil.formatHexString(data))
            }

            override fun onNotifyFailure(exception: BleException?) {
                Log.d("aa", exception.toString())
            }

            override fun onNotifySuccess() {
                Log.d("aa", "2222")
                syncDeviceTime(mac)
                controller.getSensorVersion(mac)
            }
        })
    }

    private fun sendBroadcast(sensorValue: String) {
        val localIntent = Intent("com.ihomey.linkuphome.SENSOR_VALUE_CHANGED")
        localIntent.putExtra("sensorValue", sensorValue)
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
    }

    private fun syncDeviceTime(mac:String){
        mViewDataBinding.lampDeviceBleRcvList.postDelayed(object :Runnable{
            override fun run() {
                syncTime(mac)
            }
        },1500)
    }

}