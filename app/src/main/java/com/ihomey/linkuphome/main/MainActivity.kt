package com.ihomey.linkuphome.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.Gravity
import android.widget.TextView
import com.clj.fastble.BleManager
import com.csr.mesh.ConfigModelApi
import com.csr.mesh.DataModelApi
import com.csr.mesh.GroupModelApi
import com.csr.mesh.MeshService
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.device.MeshDeviceListFragment
import com.ihomey.linkuphome.group.GroupSettingFragment
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.listener.OnLanguageListener
import com.ihomey.linkuphome.listeners.*
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.inuker.bluetooth.library.BluetoothClient
import de.keyboardsurfer.android.widget.crouton.Crouton
import java.lang.ref.WeakReference
import java.util.*


class MainActivity : BaseActivity(), BridgeListener, OnLanguageListener, IFragmentStackHolder, MeshDeviceListFragment.DevicesStateListener, MeshServiceStateListener, GroupSettingFragment.ModelUpdateListener {

    private val REMOVE_ACK_WAIT_TIME_MS = 10 * 1000L
    private val languageArray: Array<String> = arrayOf("en", "zh", "fr", "de", "es", "nl")

    private var mViewModel: MainViewModel? = null
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
    private var mModels = arrayListOf<Model>()
    private var mDeviceIdToModel: Int = 0
    private var mGroupIdToModel: Int = 0
    private var mSupportGroupNums = 0


    private var mBatteryListener: BatteryValueListener? = null

    override fun removeDevice(device: SingleDevice, listener: DeviceRemoveListener) {
        mRemovedUuidHash = device.hash
        mRemovedDeviceId = device.id
        mRemovedListener = listener
        mService?.setDeviceDiscoveryFilterEnabled(true)
        ConfigModelApi.resetDevice(device.id)
        mMeshHandler.postDelayed(removeDeviceTimeout, REMOVE_ACK_WAIT_TIME_MS)
    }

    override fun associateDevice(uuidHash: Int, shortCode: String?) {
        if (shortCode == null) {
            mService?.associateDevice(uuidHash, 0, false)
        }
    }

    override fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener?) {
        meshAssListener = if (enabled && listener != null) listener else null
        try {
            mService?.setDeviceDiscoveryFilterEnabled(enabled)
        } catch (e: Exception) {
            Log.d("LinkupHome", "you should firstly connect to bridge!")
        }
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

    override fun connectBridge() {
        mService?.setHandler(mMeshHandler)
        mService?.setLeScanCallback(mScanCallBack)
        mService?.setMeshListeningMode(true, true)
        mService?.autoConnect(1, 10000, 100, 0)
    }

    override fun onLanguageChange(languageIndex: Int) {
        val desLanguage = languageArray[languageIndex - 1]
        val currentLanguage = LocaleHelper.getLanguage(this)
        if (!TextUtils.equals(currentLanguage, desLanguage)) {
            mViewModel?.setBridgeState(false)
            LocaleHelper.setLocale(this, desLanguage)
            releaseResource()
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTranslucentStatus()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mViewModel?.getLocalSetting()?.observe(this, Observer<Resource<LampCategory>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                mService?.setNetworkPassPhrase(it.data.networkKey)
            }
        })
        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
        BleManager.getInstance().init(application)
        BleManager.getInstance().enableLog(true).setReConnectCount(1, 5000).setConnectOverTime(20000).operateTimeout = 5000;
        BluetoothClientManager.getInstance().init(application)
        if (savedInstanceState == null) {
            val hasAgreed by PreferenceHelper("hasAgreed", false)
            supportFragmentManager.beginTransaction().replace(R.id.container, if (!hasAgreed) PrivacyFragment().newInstance() else WelcomeFragment().newInstance()).commitNow()
        }
    }

    override fun replaceFragment(containerId: Int, frag: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.hold, R.anim.hold, R.anim.push_top_out)
        transaction.replace(containerId, frag, frag.javaClass.simpleName)
        transaction.addToBackStack(frag.javaClass.simpleName)
        transaction.commit()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK && data != null) {
            val categoryType = data.getIntExtra("categoryType", -1)
            if (categoryType != -1) mViewModel?.loadData(categoryType)
        }
    }

    private fun releaseResource() {
        Crouton.cancelAllCroutons()
        mService?.setDeviceDiscoveryFilterEnabled(false)
        if (mConnected) mService?.disconnectBridge()
        mService?.setHandler(null)
        mMeshHandler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed() {
        if (!handleBackPress(this)) {
            finish()
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
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
        discoverDevices(true, null)
        mMeshHandler.postDelayed({ mViewModel?.setBridgeState(mConnected) }, 550)
    }

    private fun onDisConnected(name: String) {
//        val textView = TextView(this)
//        textView.width = getScreenW()
//        textView.setPadding(0, dip2px(36f), 0, dip2px(18f))
//        textView.gravity = Gravity.CENTER
//        textView.setTextColor(resources.getColor(android.R.color.white))
//        textView.setBackgroundResource(R.color.bridge_connected_msg_bg_color)
//        textView.text = '"' + name + '"' + " " + getString(R.string.disconnected)
//        Crouton.make(this, textView).show()
        mViewModel?.setBridgeState(mConnected)
    }

    private fun getNextDeviceIndex() {
        mViewModel?.getGlobalSetting()?.observe(this, Observer<Resource<LampCategory>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                mService?.setNextDeviceId(it.data.nextDeviceIndex)
            }
        })
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

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mService = (rawBinder as MeshService.LocalBinder).service
            getNextDeviceIndex()
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mService = null
        }
    }

    private val mScanCallBack = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        mService?.processMeshAdvert(device, scanRecord, rssi)
        if (!TextUtils.isEmpty(device.name) && !addressToNameMap.containsKey(device.address)) {
            addressToNameMap[device.address] = device.name
        }
    }

    private val mMeshHandler = MeshHandler(this)

    private class MeshHandler(activity: MainActivity) : Handler() {
        private val mActivity: WeakReference<MainActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val parentActivity = mActivity.get()
            when (msg.what) {
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
                        if (numIds >= parentActivity.mSupportGroupNums) {
                            parentActivity.assignGroups(numIds)
                        } else {
                            if (parentActivity.mGroupUpdateListener != null) {
                                parentActivity.mGroupUpdateListener?.groupsUpdated(-1, -1, -1, false, parentActivity.getString(R.string.group_setting_exceed_number))
                            }
                        }
                    }
                }
                MeshService.MESSAGE_GROUP_MODEL_GROUPID -> {
                    if (parentActivity?.mGroupUpdateListener != null) {
                        msg.data.getByte(MeshService.EXTRA_GROUP_INDEX)
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
                                break
                            }
                        }
                        if (toRemove != null) {
                            parentActivity.mConnectedDevices.remove(toRemove)
                        }
                    }
                    if (numConnections == 0) {
                        if (parentActivity != null) {
                            parentActivity.mConnected = false
                            val name = parentActivity.addressToNameMap[address]
                            if (name != null && !TextUtils.isEmpty(name)) {
                                parentActivity.runOnUiThread {
                                    parentActivity.onDisConnected(name)
                                }
                            }
                        }
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
                    parentActivity?.onMessageTimeout(expectedMsg, id, meshRequestId)
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


}