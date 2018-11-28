package com.ihomey.linkuphome.device

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
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
    private var isFragmentVisible = false
    private var isDeviceConnectStateReceiverRegistered: Boolean = false
    private var setting: LampCategory? = null
    private lateinit var mViewDataBinding: FragmentDeviceBleListBinding
    private lateinit var deviceListAdapter: DeviceListAdapter
    private var mViewModel: MainViewModel? = null
    private val deviceAssociateFragment = BleDeviceAssociateFragment()
    private var connectedDeviceList = mutableListOf<SingleDevice>()
    private var autoConnectingDeviceMacList = mutableListOf<String>()
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    private val controller: BedController = BedController()

    fun newInstance(lampCategoryType: Int): BleDeviceListFragment {
        val fragment = BleDeviceListFragment()
        val bundle = Bundle()
        bundle.putInt("lampCategoryType", lampCategoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingIntent = PendingIntent.getBroadcast(context, 0, Intent("com.ihomey.linkuphome.BLE_DEVICE_CONNECT_STATE"), PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
        deviceListAdapter.onItemLongClickListener = this

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, viewType ->
            if (viewType != -1) {
                val width = Utils.dip2px(context, 48f)
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
                swipeRightMenu.addMenuItem(deleteItem)
            }
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
                if (it.data != null) {
                    connectedDeviceList.clear()
                    connectedDeviceList.addAll(it.data)
                }
                if (BleManager.getInstance().isBlueEnable) discoverDevices() else startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0x01)

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
    }


    override fun onStop() {
        super.onStop()
        isFragmentVisible = false
        registerBleDeviceConnectStateReceiver()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent)
            else -> alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, pendingIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        isFragmentVisible = true
        if (isDeviceConnectStateReceiverRegistered) context.unregisterReceiver(bleDeviceConnectStateReceiver)
        isDeviceConnectStateReceiverRegistered = false
        alarmManager.cancel(pendingIntent)
    }


    private fun registerBleDeviceConnectStateReceiver() {
        val filter = IntentFilter()
        filter.addAction("com.ihomey.linkuphome.BLE_DEVICE_CONNECT_STATE")
        context.registerReceiver(bleDeviceConnectStateReceiver, filter)
        isDeviceConnectStateReceiverRegistered = true
    }


    private val bleDeviceConnectStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, pendingIntent)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, pendingIntent)
            }
            for (singleDevice in connectedDeviceList) {
                if (!BleManager.getInstance().isConnected(singleDevice.device.macAddress)) connectBleDevice(singleDevice)
            }
        }
    }


    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        val singleDevice = deviceListAdapter.getItem(position)
        if (singleDevice != null && BleManager.getInstance().isConnected(singleDevice.device.macAddress)) {
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
                    deviceListAdapter.notifyItemChanged(position)
                }
            })
            dialog.show(activity.fragmentManager, "DeviceRenameFragment")
        }
        return true
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val singleDevice = deviceListAdapter.getItem(position)
        if (singleDevice != null) {
            if (!BleManager.getInstance().isConnected(singleDevice.device.macAddress) && !autoConnectingDeviceMacList.contains(singleDevice.device.macAddress)) {
                connectBleDevice(singleDevice, position)
            } else {
                val bleLampFragment = parentFragment as BleLampFragment
                bleLampFragment.setIsReName(false)
                mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(singleDevice.device.type, singleDevice.id))
            }
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge) {
        isDeviceRemoving = true
        showRemoveDeviceAlertDialog(menuBridge.adapterPosition)
        menuBridge.closeMenu()
    }

    private fun showRemoveDeviceAlertDialog(position: Int) {
        val singleDevice = deviceListAdapter.getItem(position)
        if (singleDevice != null) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(getString(R.string.delete) + " " + singleDevice.device.name + "?")
            builder.setPositiveButton(R.string.confirm) { _, _ ->
                deviceListAdapter.remove(position)
                mViewModel?.deleteSingleDevice(lampCategoryType, singleDevice.id)
                isDeviceRemoving = false
                BleManager.getInstance().disconnect(singleDevice.device.macAddress)
                BleManager.getInstance().cancelScan()
                mViewDataBinding.lampDeviceBleRcvList.postDelayed({ discoverDevices() }, 1000)
            }
            builder.setNegativeButton(R.string.cancel, null)
            builder.setCancelable(false)
            builder.create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0x01) {
            discoverDevices()
        }
    }

    private fun discoverDevices() {
        mViewDataBinding.llBleDeviceSearching.visibility = if (deviceListAdapter.itemCount == 0) View.VISIBLE else View.GONE
        val scanRuleConfig = BleScanRuleConfig.Builder().setServiceUuids(null).setDeviceName(true, "Linkuphome M1").setDeviceMac(null).setAutoConnect(false).setScanTimeOut(60 * 1000).build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {

            }

            override fun onScanStarted(success: Boolean) {

            }

            override fun onScanning(bleDevice: BleDevice?) {
                if (!isDeviceRemoving && bleDevice?.mac != null && bleDevice.name != null) {
                    mViewDataBinding.llBleDeviceSearching.visibility = View.GONE
                    val deviceType = DeviceType.values()[lampCategoryType]
                    val device = SingleDevice(0, Device(deviceType.name, lampCategoryType, bleDevice.mac), 0, 0, 0, 0, null)
                    val position = connectedDeviceList.indexOf(device)
                    val adapterPosition = deviceListAdapter.data.indexOf(device)
                    if (position == -1 && adapterPosition == -1) {
                        deviceListAdapter.addData(device)
                    } else {
                        if (adapterPosition == -1) deviceListAdapter.addData(connectedDeviceList[position])
                        connectBleDevice(connectedDeviceList[position])
                    }
                }
            }
        })
    }

    private fun connectBleDevice(singleDevice: SingleDevice, position: Int) {
        deviceAssociateFragment.isCancelable = false
        deviceAssociateFragment.show(activity.fragmentManager, "DeviceAssociateFragment")
        mViewDataBinding.llBleDeviceSearching.postDelayed(associateProgressChangedAction, 300)
        BleManager.getInstance().connect(singleDevice.device.macAddress, object : BleGattCallback() {
            override fun onStartConnect() {

            }

            override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                deviceAssociateFragment.dismiss()
                deviceListAdapter.notifyItemChanged(position)
            }

            override fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt?, status: Int) {
                val device = SingleDevice(setting?.nextDeviceIndex!!, Device(singleDevice.device.name, lampCategoryType, singleDevice.device.macAddress), 0, 0, 0, 0, ControlState())
                if (connectedDeviceList.indexOf(device) == -1) {
                    deviceListAdapter.getItem(position)?.id = setting?.nextDeviceIndex!!
                    mViewModel?.addSingleDevice(setting!!, device)
                }
                deviceListAdapter.notifyItemChanged(position)
                mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                deviceAssociateFragment.dismiss()
                enableNotify(bleDevice.mac)
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                mViewDataBinding.llBleDeviceSearching.removeCallbacks(associateProgressChangedAction)
                deviceAssociateFragment.dismiss()
                if (activity != null && isFragmentVisible) activity.toast(getString(R.string.device_associate_fail))
            }
        })
    }

    private fun connectBleDevice(singleDevice: SingleDevice) {
        if (!BleManager.getInstance().isConnected(singleDevice.device.macAddress)) {
            autoConnectingDeviceMacList.add(singleDevice.device.macAddress)
            BleManager.getInstance().connect(singleDevice.device.macAddress, object : BleGattCallback() {
                override fun onStartConnect() {

                }

                override fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                    autoConnectingDeviceMacList.remove(singleDevice.device.macAddress)
                    val position = deviceListAdapter.data.indexOf(singleDevice)
                    if (position != -1) {
                        deviceListAdapter.notifyItemChanged(position)
                    }
                    autoConnectingDeviceMacList.remove(singleDevice.device.macAddress)
                }

                override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
                    val position = deviceListAdapter.data.indexOf(singleDevice)
                    if (position != -1) {
                        deviceListAdapter.notifyItemChanged(position)
                    }
                    val lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_5", -1)
                    if (singleDevice.id == lastUsedDeviceId) {
                        enableNotify(singleDevice.device.macAddress)
                        val bleLampFragment = parentFragment as BleLampFragment
                        bleLampFragment.setIsReName(false)
                        mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(singleDevice.device.type, singleDevice.id))
                        if (!isFragmentVisible) {
                            val lastPushTime by PreferenceHelper("lastPushTime", 0L)
                            if ((System.currentTimeMillis() - lastPushTime) >= 60 * 1000) mViewDataBinding.lampDeviceBleRcvList.postDelayed({ controller.getAllEnvironmentValue(singleDevice.device.macAddress) }, 800)
                        }
                    }
                    autoConnectingDeviceMacList.remove(singleDevice.device.macAddress)
                }

                override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                    autoConnectingDeviceMacList.remove(singleDevice.device.macAddress)
                    if (activity != null && isFragmentVisible) activity.toast(getString(R.string.device_associate_fail))
                }
            })
        }
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

    private fun syncDeviceTime(mac: String) {
        mViewDataBinding.lampDeviceBleRcvList.postDelayed({ syncTime(mac) }, 1500)
    }

}