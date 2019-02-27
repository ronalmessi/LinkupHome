package com.ihomey.linkuphome.home

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.util.SparseIntArray
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.csr.mesh.ConfigModelApi
import com.csr.mesh.DataModelApi
import com.csr.mesh.GroupModelApi
import com.csr.mesh.MeshService
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.HomePageAdapter
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.entity.Model
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device1.ConnectDeviceFragment
import com.ihomey.linkuphome.device1.DevicesFragment
import com.ihomey.linkuphome.listener.*
import com.ihomey.linkuphome.listeners.BatteryValueListener
import com.ihomey.linkuphome.listeners.DeviceRemoveListener
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.room.UnBindedDevicesFragment
import de.keyboardsurfer.android.widget.crouton.Crouton
import kotlinx.android.synthetic.main.home_fragment.*
import java.lang.ref.WeakReference
import java.util.*


class HomeActivity : BaseActivity(), BottomNavigationVisibilityListener, BridgeListener, OnLanguageListener, MeshServiceStateListener, ConnectDeviceFragment.DevicesStateListener, DevicesFragment.DevicesStateListener, UnBindedDevicesFragment.BindDeviceListener {


    private val REMOVE_ACK_WAIT_TIME_MS = 10 * 1000L


    private val mSharedPreferences by lazy { App.instance.getSharedPreferences("LinkupHome", Context.MODE_PRIVATE) }


    val currentZoneId by PreferenceHelper("currentZoneId", -1)
    private lateinit var mViewModel: HomeActivityViewModel
    private var mService: MeshService? = null

    private var mConnected = false
    private val mDeviceIdToUuidHash = SparseIntArray()
    private val mConnectedDevices = HashSet<String>()
    private val addressToNameMap = ArrayMap<String, String>()

    private var meshAssListener: DeviceAssociateListener? = null
    private var mRemovedListener: DeviceRemoveListener? = null
    private var mRemovedUuidHash: Int = 0
    private var mRemovedDeviceId: Int = 0

    private var mGroupUpdateListener: GroupUpdateListener? = null
    private var mBindRoomId: Int = 0
    private var mBatteryListener: BatteryValueListener? = null
    private var currentZone: Zone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucentStatus()
        setContentView(R.layout.home_fragment)
        initView()
        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
        mViewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                currentZone = it.data
                if (it.data != null && !TextUtils.isEmpty(it.data.networkKey)) {
                    Log.d("aa", "-bbbbb-" + it)
                    mService?.setNetworkPassPhrase(it.data.networkKey)
                }
            }
        })
    }

    private fun initView() {
        viewPager.adapter = HomePageAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 3
        bottom_nav_view.setOnNavigationItemSelectedListener { item ->
            viewPager.currentItem = bottom_nav_view.menu.findItem(item.itemId).order
            true
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            releaseResource()
            unbindService(mServiceConnection)
        } catch (e: Exception) {
            Log.d("LinkupHome", "oh,some error happen!")
        }
    }

    override fun onBackPressed() {
        if (!handleBackPress(this)) {
            finish()
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        }
    }

    override fun showBottomNavigationBar(isVisible: Boolean) {
        bottom_nav_view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onLanguageChange(languageIndex: Int) {
        val desLanguage = AppConfig.LANGUAGE[languageIndex]
        val currentLanguage = LocaleHelper.getLanguage(this)
        if (!TextUtils.equals(currentLanguage, desLanguage)) {
            mViewModel.setBridgeState(false)
            LocaleHelper.setLocale(this, desLanguage)
            releaseResource()
            recreate()
//            reload()
        }
    }


    private fun releaseResource() {
        Crouton.cancelAllCroutons()
        mService?.setDeviceDiscoveryFilterEnabled(false)
        if (mConnected) mService?.disconnectBridge()
        mService?.setHandler(null)
        mMeshHandler.removeCallbacksAndMessages(null)
    }

    private fun scheduleScreen() {
        val finalHost = NavHostFragment.create(if (currentZoneId != -1) R.navigation.nav_zone_init_3 else R.navigation.nav_zone_init_1)
        supportFragmentManager.beginTransaction().replace(R.id.nav_host, finalHost).setPrimaryNavigationFragment(finalHost).commit()
    }


    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mService = (rawBinder as MeshService.LocalBinder).service
            getNextDeviceIndex()
            connectBridge()
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mService = null
        }
    }

    private fun getNextDeviceIndex() {
        mViewModel.getGlobalSetting().observe(this, Observer<Resource<Setting>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                Log.d("aa", "hhahaasdasdad---" + it.data.nextDeviceIndex)
                mService?.setNextDeviceId(it.data.nextDeviceIndex)
                mSharedPreferences.intLiveData("currentZoneId", -1).observe(this, Observer {
                    mViewModel.setCurrentZoneId(it)
                })
            }
        })
    }

    override fun connectBridge() {
        mService?.setHandler(mMeshHandler)
        mService?.setLeScanCallback(mScanCallBack)
        mService?.setMeshListeningMode(true, true)
        mService?.autoConnect(1, 10000, 100, 0)
    }

    private val mScanCallBack = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        mService?.processMeshAdvert(device, scanRecord, rssi)
        if (!TextUtils.isEmpty(device.name) && !addressToNameMap.containsKey(device.address)) {
            addressToNameMap[device.address] = device.name
        }
    }

    private val mMeshHandler = MeshHandler(this)

    private class MeshHandler(activity: HomeActivity) : Handler() {
        private val mActivity: WeakReference<HomeActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val parentActivity = mActivity.get()
            when (msg.what) {
                MeshService.MESSAGE_REQUEST_BT -> {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    parentActivity?.startActivityForResult(enableBtIntent, REQUEST_BT_RESULT_CODE)
                }
                MeshService.MESSAGE_LE_CONNECTED -> {
                    val address = msg.data.getString(MeshService.EXTRA_DEVICE_ADDRESS)
                    if (parentActivity != null) {
                        parentActivity.mConnectedDevices.add(address)
                        val name = parentActivity.addressToNameMap[address]
                        if (!parentActivity.mConnected && name != null && !TextUtils.isEmpty(name)) {
                            parentActivity.runOnUiThread {
                                parentActivity.onConnected(name)
                            }
                        }
                    }
                }
                MeshService.MESSAGE_DEVICE_DISCOVERED -> {
                    val uuidHash = msg.data.getInt(MeshService.EXTRA_UUIDHASH_31)
                    if (parentActivity?.mRemovedListener != null && parentActivity.mRemovedUuidHash == uuidHash) {
                        parentActivity.mRemovedListener?.onDeviceRemoved(parentActivity.mRemovedDeviceId, uuidHash, true)
                        parentActivity.mRemovedListener = null
                        parentActivity.mRemovedUuidHash = 0
                        parentActivity.mRemovedDeviceId = 0
                        parentActivity.mService?.setDeviceDiscoveryFilterEnabled(false)
                        removeCallbacks(parentActivity.removeDeviceTimeout)
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
                        parentActivity.assignGroups(deviceId, numIds)
                    }
                }
                MeshService.MESSAGE_GROUP_MODEL_GROUPID -> {
                    if (parentActivity?.mGroupUpdateListener != null) {
                        val index = msg.data.getByte(MeshService.EXTRA_GROUP_INDEX).toInt()
                        val groupId = msg.data.getInt(MeshService.EXTRA_GROUP_ID)
                        val deviceId = msg.data.getInt(MeshService.EXTRA_DEVICE_ID)
                        parentActivity.mGroupUpdateListener?.groupsUpdated(deviceId, groupId, index, true, null)
                    }
                }
                MeshService.MESSAGE_TIMEOUT -> {
                    val expectedMsg = msg.data.getInt(MeshService.EXTRA_EXPECTED_MESSAGE)
                    var id = if (msg.data.containsKey(MeshService.EXTRA_UUIDHASH_31)) {
                        msg.data.getInt(MeshService.EXTRA_UUIDHASH_31)
                    } else {
                        msg.data.getInt(MeshService.EXTRA_DEVICE_ID)
                    }
                    val meshRequestId = msg.data.getInt(MeshService.EXTRA_MESH_REQUEST_ID)
                    parentActivity?.onMessageTimeout(expectedMsg, id, meshRequestId)
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
            }
        }
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
                    meshAssListener?.deviceAssociated(-1, getString(R.string.device_associate_fail))
                }
            }
        }
    }

    private fun onConnected(name: String) {
        mConnected = true
        val textView = TextView(this)
        textView.width = getScreenW()
        textView.setPadding(0, dip2px(36f), 0, dip2px(18f))
        textView.gravity = Gravity.CENTER
        textView.setTextColor(resources.getColor(android.R.color.white))
        textView.setBackgroundResource(R.color.bridge_connected_msg_bg_color)
        textView.text = '"' + name + '"' + " " + getString(R.string.state_connected)
        Crouton.make(this, textView).show()
//        discoverDevices(true, null)
        mMeshHandler.postDelayed({ mViewModel.setBridgeState(mConnected) }, 550)
    }

    private fun onDisConnected(name: String) {
        mViewModel.setBridgeState(mConnected)
    }


    override fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener?) {
        meshAssListener = if (enabled && listener != null) listener else null
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

    override fun removeDevice(deviceId: Int, deviceHash: Int, listener: DeviceRemoveListener) {
        mRemovedUuidHash = deviceHash
        mRemovedDeviceId = deviceId
        mRemovedListener = listener
        mService?.setDeviceDiscoveryFilterEnabled(true)
        ConfigModelApi.resetDevice(deviceId)
        mMeshHandler.postDelayed(removeDeviceTimeout, REMOVE_ACK_WAIT_TIME_MS)
    }

    override fun isMeshServiceConnected(): Boolean {
        return mConnected
    }

    override fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener) {
        mBatteryListener = batteryValueListener
        if (mConnected) {
            DataModelApi.sendData(deviceId, decodeHex("B600B6".toCharArray()), false)
        }
    }

    override fun bindDevice(deviceId: Int, groupId: Int, groupUpdateListener: GroupUpdateListener?) {
        this.mGroupUpdateListener = groupUpdateListener
        if (deviceId != -1) {
            mBindRoomId = groupId
            GroupModelApi.getNumModelGroupIds(deviceId, DataModelApi.MODEL_NUMBER)
        }
    }

    private fun assignGroups(deviceId: Int, maxNum: Int) {
        val mModelsToQueryForGroups = IntArray(maxNum)
        currentZone?.id?.let {
            mViewModel.getModels(deviceId, it).observe(this, Observer<Resource<List<Model>>> {
                if (it?.status == Status.SUCCESS && it.data != null) {
                    if (it.data.size <= maxNum) {
                        var unBindingGroupIndex = -1
                        for (model in it.data) {
                            if (model.roomId == mBindRoomId && model.deviceId == deviceId) unBindingGroupIndex = model.groupIndex
                            mModelsToQueryForGroups[model.groupIndex] = 1
                        }
                        if (unBindingGroupIndex != -1) {
                            GroupModelApi.setModelGroupId(deviceId, DataModelApi.MODEL_NUMBER, unBindingGroupIndex, 0, 0)
                        } else {
                            var groupIndex = 0
                            for (index in 0 until mModelsToQueryForGroups.size) {
                                if (mModelsToQueryForGroups[index] == 0) {
                                    groupIndex = index
                                    break
                                }
                            }
                            GroupModelApi.setModelGroupId(deviceId, DataModelApi.MODEL_NUMBER, groupIndex, 0, mBindRoomId)
                        }
                    } else {
                        if (mGroupUpdateListener != null) {
                            mGroupUpdateListener?.groupsUpdated(-1, -1, -1, false, getString(R.string.group_setting_exceed_number))
                        }
                    }
                }
            })
        }

    }

}
