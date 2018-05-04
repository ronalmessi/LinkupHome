package com.ihomey.linkuphome.control

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.annotation.RequiresApi
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.util.ArrayMap
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.util.SparseIntArray
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.csr.mesh.ConfigModelApi
import com.csr.mesh.DataModelApi
import com.csr.mesh.GroupModelApi
import com.csr.mesh.MeshService
import com.devspark.appmsg.AppMsg
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.ControlDeviceListAdapter
import com.ihomey.linkuphome.adapter.ControlPageAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device.MeshDeviceListFragment
import com.ihomey.linkuphome.group.GroupSettingFragment
import com.ihomey.linkuphome.listeners.*
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class MeshControlActivity : BaseControlActivity(), BottomNavigationView.OnNavigationItemSelectedListener, MeshDeviceListFragment.DevicesStateListener, MeshServiceStateListener, BaseQuickAdapter.OnItemClickListener, BottomNavigationView.OnNavigationItemReselectedListener, GroupSettingFragment.ModelUpdateListener {

    private var lampCategoryType: Int = -1
    private val REMOVE_ACK_WAIT_TIME_MS = 10 * 1000L

    private lateinit var behavior: BottomSheetBehavior<View>
    private var mViewModel: MeshControlViewModel? = null

    private lateinit var mViewPager: ViewPager
    private lateinit var mBottomNavigationView: BottomNavigationView

    private var mService: MeshService? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null

    private val adapter = ControlDeviceListAdapter(R.layout.control_device_list_item)

    var mConnectedDevices = HashSet<String>()
    private val addressToNameMap = ArrayMap<String, String>()
    private val mDeviceIdToUuidHash = SparseIntArray()
    private var mConnected = false

    private var meshAssListener: DeviceAssociateListener? = null
    private var mRemovedListener: DeviceRemoveListener? = null
    private var mRemovedUuidHash: Int = 0
    private var mRemovedDeviceId: Int = 0

    private var mGroupUpdateListener: GroupUpdateListener? = null
    private var mModels = arrayListOf<Model>()
    private var mDeviceIdToModel: Int = 0
    private var mGroupIdToModel: Int = 0
    private var mSupportGroupNums = 0

    private var mBatteryListener: BatteryValueListener? = null

    override fun initData() {
        lampCategoryType = intent.getIntExtra("lampCategoryType", -1)
        mViewModel = ViewModelProviders.of(this).get(MeshControlViewModel::class.java)
        mViewModel?.getSettingResults()?.observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS && it.data?.size == 2) {
                loadSetting(it.data[1].networkKey, it.data[0].nextDeviceIndex)
            }
        })
        mViewModel?.getControlDeviceResults()?.observe(this, Observer<Resource<List<ControlDevice>>> {
            if (it?.status == Status.SUCCESS) {
                val newData = arrayListOf<ControlDevice>()
                newData.addAll(it.data!!)
                newData.add(ControlDevice(-1, Device(getString(R.string.initialize), -1), ControlState()))
                adapter.setNewData(newData)
            }
        })
        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun initViewPager(viewPager: ViewPager, controlBaseBnv: BottomNavigationView) {
        mViewPager = viewPager
        mBottomNavigationView = controlBaseBnv
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = ControlPageAdapter(lampCategoryType, supportFragmentManager)
        controlBaseBnv.setOnNavigationItemSelectedListener(this)
        controlBaseBnv.setOnNavigationItemReselectedListener(this)
        initControlDeviceSelectionDialog()
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                controlBaseBnv.selectedItemId = R.id.item_tab_mesh_control
                mViewModel?.setLastUsedDeviceId(it.data.id, lampCategoryType)
            }
        })
    }

    private fun initControlDeviceSelectionDialog() {
        mViewDataBinding.controlDeviceRcvList.setPadding(dip2px(8f), dip2px(8f), dip2px(8f), dip2px(8f))
        mViewDataBinding.controlDeviceRcvList.layoutManager = LinearLayoutManager(this)
        mViewDataBinding.controlDeviceRcvList.addItemDecoration(SpaceItemDecoration(dip2px(1f)))
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
        adapter.onItemClickListener = this
    }

    private fun hideControlDeviceSelectionDialog() {
        val menuView = mBottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val item = menuView.getChildAt(mViewPager.currentItem) as BottomNavigationItemView
        mBottomNavigationView.selectedItemId = item.id
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_tab_mesh_control -> {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                setPageItem(0)
            }
            R.id.item_tab_mesh_device -> {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                setPageItem(1)
            }
            R.id.item_tab_mesh_group -> {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                setPageItem(2)
            }
            R.id.item_tab_mesh_controls -> behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return true
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        if (item.itemId == R.id.item_tab_mesh_controls) {
            hideControlDeviceSelectionDialog()
        }
    }

    override fun onBackPressed() {
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            hideControlDeviceSelectionDialog()
        } else if (!handleBackPress(this)) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                mBluetoothAdapter = manager.adapter
                if (mBluetoothAdapter?.isEnabled!!) {
                    mBluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner
                    if (mBluetoothLeScanner != null) {
                        mBluetoothLeScanner?.stopScan(mScanCallback)
                    }
                }
            }
            mService?.disconnectBridge()
        } catch (e: Exception) {
            Log.d("LinkupHome", "oh,some error!")
        } finally {
            Log.d("aa", "111111")
            mService?.setHandler(null)
            mMeshHandler.removeCallbacksAndMessages(null)
            unbindService(mServiceConnection)
            mService = null
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!mConnected) {
            connect()
        }
    }

    override fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener) {
        meshAssListener = if (enabled) listener else null
        try {
            mService?.setDeviceDiscoveryFilterEnabled(enabled)
        } catch (e: Exception) {
            Log.d("LinkupHome", "you should firstly connect to bridge!")
        }
    }

    override fun associateDevice(uuidHash: Int, shortCode: String?) {
        if (shortCode == null) {
            mService?.associateDevice(uuidHash, 0, false)
        }
    }

    override fun isMeshServiceConnected(): Boolean {
        return mConnected
    }

    override fun updateModel(deviceId: Int, groupId: Int, models: List<Model>?, groupUpdateListener: GroupUpdateListener) {
        mGroupUpdateListener = groupUpdateListener
        mDeviceIdToModel = deviceId
        mGroupIdToModel = groupId
        mModels.clear()
        if (models != null) {
            mModels.addAll(models)
            mSupportGroupNums = mModels.size
        }
        GroupModelApi.getNumModelGroupIds(deviceId, DataModelApi.MODEL_NUMBER)
    }

    override fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener) {
        mBatteryListener = batteryValueListener
        if (mConnected) {
            DataModelApi.sendData(deviceId, decodeHex("B600B6".toCharArray()), false)
        }
    }

    private fun assignGroups(maxNums: Int) {
        val mModelsToQueryForGroups = IntArray(maxNums)
        var unBindingGroupIndex = -1
        for (model in mModels) {
            if (model.groupId == mGroupIdToModel && model.deviceId == mDeviceIdToModel) unBindingGroupIndex = model.groupIndex
            mModelsToQueryForGroups[model.groupIndex] = 1
        }
        if (unBindingGroupIndex != -1) {
            GroupModelApi.setModelGroupId(mDeviceIdToModel, DataModelApi.MODEL_NUMBER, unBindingGroupIndex, 0, 0)
        } else {
            var groupIndex = 0
            for (index in 0 until mModelsToQueryForGroups.size) {
                if (mModelsToQueryForGroups[index] == 0) {
                    groupIndex = index
                }
            }
            GroupModelApi.setModelGroupId(mDeviceIdToModel, DataModelApi.MODEL_NUMBER, groupIndex, 0, mGroupIdToModel)
        }

    }

    override fun removeDevice(device: SingleDevice, listener: DeviceRemoveListener) {
        mRemovedUuidHash = device.hash
        mRemovedDeviceId = device.id
        mRemovedListener = listener
        mService?.setDeviceDiscoveryFilterEnabled(true)
        ConfigModelApi.resetDevice(device.id)
        mMeshHandler.postDelayed(removeDeviceTimeout, REMOVE_ACK_WAIT_TIME_MS)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        hideControlDeviceSelectionDialog()
        val controlDevice = adapter?.getItem(position) as ControlDevice
        if (controlDevice.id != -1) {
            mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(controlDevice.device.type, controlDevice.id))
        } else {
            val isShare by PreferenceHelper("share_$lampCategoryType", false)
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.devices_clear)
            builder.setPositiveButton(R.string.confirm) { _, _ ->
                if (isShare) {
                    mViewModel?.removeAllDevices(lampCategoryType)
                } else {
                    mViewModel?.removeAllGroupDevices(lampCategoryType)
                }
            }
            builder.setNegativeButton(R.string.cancel, null)
            builder.setCancelable(false)
            builder.create().show()
        }

    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            Log.d("aa", "onServiceConnected")
            mService = (rawBinder as MeshService.LocalBinder).service
            if (mService != null) {
                mViewModel?.loadSetting(lampCategoryType)
                connect()
            }
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            Log.d("aa", "onServiceDisconnected")
            mService = null
        }
    }


    private fun loadSetting(netWorkKey: String, nextDeviceIndex: Int) {
        mService?.setNetworkPassPhrase(netWorkKey)
        mService?.setNextDeviceId(nextDeviceIndex)
    }

    private val removeDeviceTimeout = Runnable {
        if (mRemovedListener != null) {
            mRemovedListener?.onDeviceRemoved(mRemovedDeviceId, mRemovedUuidHash, true)
            mRemovedListener = null
            mRemovedUuidHash = 0
            mRemovedDeviceId = 0
            mService?.setDeviceDiscoveryFilterEnabled(false)
        }
    }

    private fun connect() {
        mService?.setHandler(mMeshHandler)
        mService?.setLeScanCallback(mLeScanCallback)
        mService?.setMeshListeningMode(true, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val filter = ScanFilter.Builder().build()
            val filters = ArrayList<ScanFilter>()
            filters.add(filter)
            val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
            val manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = manager.adapter
            if (mBluetoothAdapter?.isEnabled!!) {
                mBluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner
                if (mBluetoothLeScanner != null) {
                    mBluetoothLeScanner?.startScan(filters, scanSettings, mScanCallback)
                }
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_BT_RESULT_CODE)
            }
        } else {
            mService?.autoConnect(1, 1000, 1000, 0)
        }
    }


    private fun onConnected(name: String) {
        mConnected = true
        val appMsg = AppMsg.makeText(this, '"' + name + '"' + " " + getString(R.string.state_connected), AppMsg.Style(AppMsg.LENGTH_SHORT, R.color.bridge_connected_msg_bg_color))
        appMsg.setLayoutGravity(Gravity.TOP)
        appMsg.show()
        mMeshHandler.postDelayed({ mViewModel?.setBridgeConnecState(mConnected) }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            val lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$lampCategoryType", -1)
            mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(lampCategoryType, lastUsedDeviceId))
            mViewModel?.loadSetting(lampCategoryType)
            mViewModel?.loadGroups(lampCategoryType)
            connect()
        } else if (requestCode == REQUEST_BT_RESULT_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val filter = ScanFilter.Builder().build()
                val filters = ArrayList<ScanFilter>()
                filters.add(filter)
                val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
                mBluetoothLeScanner = mBluetoothAdapter?.bluetoothLeScanner
                if (mBluetoothLeScanner != null) {
                    mBluetoothLeScanner?.startScan(filters, scanSettings, mScanCallback)
                }
            }
        }
    }

    private fun onMessageTimeout(expectedMessage: Int, id: Int, meshRequestId: Int) {
        when (expectedMessage) {
            MeshService.MESSAGE_GROUP_NUM_GROUPIDS -> {
                if (mGroupUpdateListener != null) {
                    mGroupUpdateListener?.groupsUpdated(id, -1, -1, false, null)
                }
            }
            MeshService.MESSAGE_GROUP_MODEL_GROUPID -> {
                if (mGroupUpdateListener != null) {
                    mGroupUpdateListener?.groupsUpdated(id, -1, -1, false, null)
                }
            }
            MeshService.MESSAGE_DEVICE_ASSOCIATED, MeshService.MESSAGE_CONFIG_MODELS -> {
                if (meshAssListener != null) {
                    meshAssListener?.deviceAssociated(-1, getString(R.string.association_failed))
                }
            }
            MeshService.MESSAGE_CONFIG_DEVICE_INFO -> {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val mScanCallback = ScScanCallback(this)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private class ScScanCallback(activity: MeshControlActivity) : ScanCallback() {
        private val mActivity: WeakReference<MeshControlActivity> = WeakReference(activity)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val parentActivity = mActivity.get()
            if (!TextUtils.isEmpty(result.device.name) && parentActivity?.mConnectedDevices?.size == 0 && result.device.name.startsWith("Linkuphome")) {
                parentActivity.mService?.connectBridge(result.device)
                parentActivity.mConnectedDevices.add(result.device.address)
                parentActivity.onConnected(result.device.name)
            }
            if (parentActivity?.mConnectedDevices?.size!! > 0) {
                parentActivity.mBluetoothLeScanner?.stopScan(this)
            }
        }
    }


    private val mLeScanCallback = LeScanCallback(this)

    private class LeScanCallback(activity: MeshControlActivity) : BluetoothAdapter.LeScanCallback {
        private val mActivity: WeakReference<MeshControlActivity> = WeakReference(activity)

        override fun onLeScan(device: BluetoothDevice, rssi: Int, scanRecord: ByteArray) {
            val parentActivity = mActivity.get()
            if (!TextUtils.isEmpty(device.name) && device.name.startsWith("Linkuphome")) {
                parentActivity?.addressToNameMap!![device.address] = device.name
                parentActivity.mService?.processMeshAdvert(device, scanRecord, rssi)
            }
        }

    }

    private val mMeshHandler = MeshHandler(this)

    private class MeshHandler(activity: MeshControlActivity) : Handler() {
        private val mActivity: WeakReference<MeshControlActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val parentActivity = mActivity.get()
            when (msg.what) {
                MeshService.MESSAGE_LE_CONNECTED -> {
                    val address = msg.data.getString(MeshService.EXTRA_DEVICE_ADDRESS)
                    parentActivity?.mConnectedDevices?.add(address)
                    if (!parentActivity?.mConnected!!) {
                        parentActivity.onConnected(parentActivity.addressToNameMap[address]!!)
                    }
                }
                MeshService.MESSAGE_DEVICE_DISCOVERED -> {
                    val uuid = msg.data.getParcelable(MeshService.EXTRA_UUID) as ParcelUuid
                    val uuidHash = msg.data.getInt(MeshService.EXTRA_UUIDHASH_31)
                    val rssi = msg.data.getInt(MeshService.EXTRA_RSSI)
                    val ttl = msg.data.getInt(MeshService.EXTRA_TTL)
                    if (parentActivity?.mRemovedListener != null && parentActivity.mRemovedUuidHash == uuidHash) {
                        parentActivity.mRemovedListener?.onDeviceRemoved(parentActivity.mRemovedDeviceId, uuidHash, true)
                        parentActivity.mRemovedListener = null
                        parentActivity.mRemovedUuidHash = 0
                        parentActivity.mRemovedDeviceId = 0
                        parentActivity.mService?.setDeviceDiscoveryFilterEnabled(false)
                        removeCallbacks(parentActivity.removeDeviceTimeout)
                    } else if (parentActivity?.meshAssListener != null) {
                        parentActivity.meshAssListener?.newUuid(uuid.uuid, uuidHash, rssi, ttl)
                    }
                }
                MeshService.MESSAGE_DEVICE_APPEARANCE -> {
                    val appearance = msg.data.getByteArray(MeshService.EXTRA_APPEARANCE)
                    val shortName = msg.data.getString(MeshService.EXTRA_SHORTNAME)
                    val uuidHash = msg.data.getInt(MeshService.EXTRA_UUIDHASH_31)
                    parentActivity?.meshAssListener?.newAppearance(uuidHash, appearance, shortName)
                }
                MeshService.MESSAGE_ASSOCIATING_DEVICE -> {
                    val progress = msg.data.getInt(MeshService.EXTRA_PROGRESS_INFORMATION)
                    parentActivity?.meshAssListener?.associationProgress(progress)
                }
                MeshService.MESSAGE_DEVICE_ASSOCIATED -> {
                    val deviceId = msg.data.getInt(MeshService.EXTRA_DEVICE_ID)
                    val uuidHash = msg.data.getInt(MeshService.EXTRA_UUIDHASH_31)
                    parentActivity?.mDeviceIdToUuidHash?.put(deviceId, uuidHash)
                    ConfigModelApi.getInfo(deviceId, ConfigModelApi.DeviceInfo.MODEL_LOW)
                }
                MeshService.MESSAGE_CONFIG_DEVICE_INFO -> {
                    val deviceId = msg.data.getInt(MeshService.EXTRA_DEVICE_ID)
                    val uuidHash = parentActivity?.mDeviceIdToUuidHash?.get(deviceId)
                    val infoType = ConfigModelApi.DeviceInfo.values()[msg.data.getByte(MeshService.EXTRA_DEVICE_INFO_TYPE).toInt()]
                    if (infoType == ConfigModelApi.DeviceInfo.MODEL_LOW) {
                        val bitmap = msg.data.getLong(MeshService.EXTRA_DEVICE_INFORMATION)
                        if (uuidHash != 0) {
                            parentActivity?.mDeviceIdToUuidHash?.removeAt(parentActivity.mDeviceIdToUuidHash.indexOfKey(deviceId))
                            parentActivity?.meshAssListener?.deviceAssociated(deviceId, uuidHash!!, bitmap)
                        }
                    }
                }
                MeshService.MESSAGE_GROUP_NUM_GROUPIDS -> {
                    if (parentActivity?.mGroupUpdateListener != null) {
                        val numIds = msg.data.getByte(MeshService.EXTRA_NUM_GROUP_IDS).toInt()
                        val modelNo = msg.data.getByte(MeshService.EXTRA_MODEL_NO).toInt()
                        val deviceId = msg.data.getInt(MeshService.EXTRA_DEVICE_ID)
                        if (numIds >= parentActivity.mSupportGroupNums) {
                            parentActivity.assignGroups(numIds)
                        } else {
                            if (parentActivity.mGroupUpdateListener != null) {
                                parentActivity.mGroupUpdateListener?.groupsUpdated(-1, -1, -1, false, parentActivity.getString(R.string.group_max_fail))
                            }
                        }
                    }
                }
                MeshService.MESSAGE_GROUP_MODEL_GROUPID -> {
                    if (parentActivity?.mGroupUpdateListener != null) {
                        val index = msg.data.getByte(MeshService.EXTRA_GROUP_INDEX).toInt()
                        val groupId = msg.data.getInt(MeshService.EXTRA_GROUP_ID)
                        parentActivity.mGroupUpdateListener?.groupsUpdated(parentActivity.mDeviceIdToModel, groupId, index, true, null)
                    }
                }

                MeshService.MESSAGE_RECEIVE_BLOCK_DATA -> {
                    if (parentActivity?.mBatteryListener != null) {
                        val deviceId = msg.data.getInt(MeshService.EXTRA_DEVICE_ID)
                        val data = msg.data.getByteArray(MeshService.EXTRA_DATA)
                        val batteryInfo = encodeHexStr(data)
                        if (batteryInfo.startsWith("b6") && parentActivity.mBatteryListener != null) {
                            val batteryLevel = batteryInfo.substring(batteryInfo.length - 2, batteryInfo.length)
                            val level = toDigit(batteryLevel[0], 1) * 16 + toDigit(batteryLevel[1], 1)
                            parentActivity.mBatteryListener?.onBatteryLevelReceived(deviceId, level)
                        }
                    }
                }

                MeshService.MESSAGE_LE_DISCONNECTED -> {
                    val numConnections = msg.data.getInt(MeshService.EXTRA_NUM_CONNECTIONS)
                    val address = msg.data.getString(MeshService.EXTRA_DEVICE_ADDRESS)
                    if (address != null) {
                        var toRemove: String? = null
                        for (s in parentActivity?.mConnectedDevices!!) {
                            if (s.compareTo(address) == 0) {
                                toRemove = s
                                break;
                            }
                        }
                        if (toRemove != null) {
                            parentActivity.mConnectedDevices.remove(toRemove)
                        }
                    }
                    if (numConnections == 0) {
                        parentActivity?.mConnected = false
                        parentActivity?.connect()
                    }
                }

                MeshService.MESSAGE_LE_DISCONNECT_COMPLETE -> {
                    parentActivity?.finish()
                }
                MeshService.MESSAGE_REQUEST_BT -> {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    parentActivity?.startActivityForResult(enableBtIntent, REQUEST_BT_RESULT_CODE)
                }

                MeshService.MESSAGE_TIMEOUT -> {
                    val expectedMsg = msg.data.getInt(MeshService.EXTRA_EXPECTED_MESSAGE)
                    var id = if (msg.data.containsKey(MeshService.EXTRA_UUIDHASH_31)) {
                        msg.data.getInt(MeshService.EXTRA_UUIDHASH_31)
                    } else {
                        msg.data.getInt(MeshService.EXTRA_DEVICE_ID)
                    }
                    val meshRequestId = msg.data.getInt(MeshService.EXTRA_MESH_REQUEST_ID)
                    Log.d("aa", "MESSAGE_TIMEOUT==" + expectedMsg + "===" + id + "----" + meshRequestId)
                    parentActivity?.onMessageTimeout(expectedMsg, id, meshRequestId)
                }

            }
        }
    }

}