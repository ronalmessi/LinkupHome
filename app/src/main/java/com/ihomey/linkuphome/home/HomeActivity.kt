package com.ihomey.linkuphome.home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.csr.mesh.ConfigModelApi
import com.csr.mesh.DataModelApi
import com.csr.mesh.MeshService
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.alarm.EnvironmentalIndicatorsFragment
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.RemoveDeviceVo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device1.ConnectDeviceFragment
import com.ihomey.linkuphome.device1.ConnectM1DeviceFragment
import com.ihomey.linkuphome.dialog.PermissionPromptDialogFragment
import com.ihomey.linkuphome.listener.*
import com.ihomey.linkuphome.listeners.BatteryValueListener
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.spp.BluetoothSPP
import de.keyboardsurfer.android.widget.crouton.Crouton
import kotlinx.android.synthetic.main.home_activity.*
import org.spongycastle.util.encoders.Hex
import java.lang.ref.WeakReference
import java.util.*


class HomeActivity : BaseActivity(), BridgeListener, OnLanguageListener, MeshServiceStateListener, ConnectDeviceFragment.DevicesStateListener, ConnectM1DeviceFragment.DevicesStateListener, EnvironmentalIndicatorsFragment.EnvironmentalIndicatorsListener {

    private val REMOVE_ACK_WAIT_TIME_MS = 4 * 1000L

    private lateinit var mViewModel: HomeActivityViewModel
    private var mService: MeshService? = null

    private var mConnected = false
    private val mDeviceIdToUuidHash = SparseIntArray()
    private val mConnectedDevices = HashSet<String>()
    private val addressToNameMap = ArrayMap<String, String>()

    private var meshAssListener: DeviceAssociateListener? = null
    private var sppStateListener: SppStateListener? = null
    private var emtValueListener: EmtValueListener? = null
    private var mBatteryListener: BatteryValueListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucentStatus()
        setContentView(R.layout.home_activity)
        initNavController()
        BluetoothSPP.getInstance().initialize(applicationContext)
        initSppService()
        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
        initViewModel()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                it.data?.nextDeviceIndex?.let { it1 -> mService?.setNextDeviceId(it1) }
                it.data?.netWorkKey?.let { it1 -> mService?.setNetworkPassPhrase(it1) }
            }
        })
        mViewModel.mRemoveDeviceVo.observe(this, Observer<RemoveDeviceVo> {
            if (it != null) {
                ConfigModelApi.resetDevice(it.deviceInstructId)
                mMeshHandler.postDelayed({
                    it.deviceRemoveListener.onDeviceRemoved(it.deviceId, it.deviceInstructId, true)
                }, REMOVE_ACK_WAIT_TIME_MS)
            }
        })
        mViewModel.setCurrentZoneId(intent?.extras?.getInt("currentZoneId"))
    }

    private fun initNavController() {
        val navHostFragment = nav_host_home as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_home)
        graph.startDestination = if (intent?.extras?.getInt("currentZoneId") != 0) R.id.homeFragment else R.id.createZoneFragment
        navHostFragment.navController.graph = graph
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
        val homeFragment = nav_host_home.childFragmentManager.fragments[0]
        if (!handleBackPress(homeFragment)) {
            finish()
            Process.killProcess(Process.myPid())
            System.exit(0)
        }
    }

    override fun onLanguageChange(languageIndex: Int) {
        mViewModel.setRemoveDeviceFlag(false)
        val desLanguage = AppConfig.LANGUAGE[languageIndex]
        val currentLanguage = LocaleHelper.getLanguage(this)
        if (!TextUtils.equals(currentLanguage, desLanguage)) {
            mViewModel.setBridgeState(false)
            LocaleHelper.setLocale(this, desLanguage)
            releaseResource()
            recreate()
        }
    }


    private fun releaseResource() {
        Crouton.cancelAllCroutons()
        mService?.setDeviceDiscoveryFilterEnabled(false)
        if (mConnected) mService?.disconnectBridge()
        mService?.setHandler(null)
        mMeshHandler.removeCallbacksAndMessages(null)
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mService = (rawBinder as MeshService.LocalBinder).service
            connectBridge()
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mService = null
        }
    }

    override fun connectBridge() {
        mService?.setHandler(mMeshHandler)
        mService?.setLeScanCallback(mScanCallBack)
        mService?.setMeshListeningMode(true, true)
        mService?.autoConnect(1, 10000, 100, 0)
    }

    private fun initSppService() {
        BluetoothSPP.getInstance()?.startService()
        BluetoothSPP.getInstance()?.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
            override fun onDeviceConnecting(name: String?, address: String?) {
                Log.d("aa", "--$name---$address---onDeviceConnecting")
            }

            override fun onDeviceConnectFailed(name: String, address: String) {
                Log.d("aa", "--$name---$address---onDeviceConnectFailed")
                BluetoothSPP.getInstance()?.autoConnectDeviceAddressList?.let {
                    if(!it.contains(address)) sppStateListener?.deviceAssociated(false, name, address)
                }
            }

            override fun onDeviceConnected(name: String, address: String) {
                Log.d("aa", "--$name---$address---onDeviceConnected")
                BluetoothSPP.getInstance()?.autoConnectDeviceAddressList?.let {
                    if(!it.contains(address)) sppStateListener?.deviceAssociated(true, name, address)
                }
            }
        })
        BluetoothSPP.getInstance()?.setOnDataReceivedListener { data, message ->
            Log.d("aa", "---" + Hex.toHexString(data))
            val receiveDataStr = Hex.toHexString(data).toUpperCase()
            if (TextUtils.equals("FE01D101DA0004C2010301CB16", receiveDataStr)) {
                toast("已开启睡眠模式", Toast.LENGTH_SHORT)
            } else if (TextUtils.equals("FE01D101DA0004C2010300CA16", receiveDataStr)) {
                toast("已取消睡眠模式", Toast.LENGTH_SHORT)
            } else if (TextUtils.equals("FE01D101DA0004C7010101CE16", receiveDataStr)) {
                toast("已开启手势控制", Toast.LENGTH_SHORT)
            } else if (TextUtils.equals("FE01D101DA0004C7010100CD16", receiveDataStr)) {
                toast("已取消手势控制", Toast.LENGTH_SHORT)
            } else if (TextUtils.equals("FE01D101DA000AC3012000000000000000EE16", receiveDataStr)) {
                toast("时间已同步", Toast.LENGTH_SHORT)
            } else if (receiveDataStr.startsWith("FE01D101DA0004C20601")) {
                val alarmId = Integer.parseInt(receiveDataStr.substring(20, 22), 16)
                toast("定时" + alarmId + "设置成功", Toast.LENGTH_SHORT)
            } else if (receiveDataStr.startsWith("FE01D101DA0004C20602")) {
                val alarmId = Integer.parseInt(receiveDataStr.substring(20, 22), 16)
                toast("定时" + alarmId + "已关闭", Toast.LENGTH_SHORT)
            } else if (receiveDataStr.startsWith("FE01D101DA0003C401")) {
                val alarmId = Integer.parseInt(receiveDataStr.substring(18, 20), 16)
                showCustomToast("闹钟" + alarmId + "已开启")
            } else if (receiveDataStr.startsWith("FE01D101DA0003C402")) {
                val alarmId = Integer.parseInt(receiveDataStr.substring(18, 20), 16)
                showCustomToast("闹钟" + alarmId + "已关闭")
            } else if (receiveDataStr.startsWith("FE01D101DA0004C1F")) {
                val sensorType = if (receiveDataStr.startsWith("FE01D101DA0004C1F2F2F2")) 1 else 0
                toast("当前床头灯型号为：$sensorType", Toast.LENGTH_SHORT)
            } else if (receiveDataStr.startsWith("FE01D101DA000BC107")) {
                val pm25Value = Integer.parseInt(receiveDataStr.substring(18, 20), 16) * 256 + Integer.parseInt(receiveDataStr.substring(20, 22), 16)
                val hchoValue = Integer.parseInt(receiveDataStr.substring(22, 24), 16) * 256 + Integer.parseInt(receiveDataStr.substring(24, 26), 16)
                val vocValue = Integer.parseInt(receiveDataStr.substring(26, 28), 16)
                emtValueListener?.onEmtValueChanged(pm25Value, hchoValue, vocValue)
            }
        }
        BluetoothSPP.getInstance()?.setBluetoothStateListener(object : BluetoothSPP.BluetoothStateListener {
            override fun onServerStartListen() {
                Log.d("aa", "onServerStartListen")
            }

            override fun onDeviceDisConnected(name: String?, address: String?) {
                Log.d("aa", "--$name---$address---onDeviceDisConnected")
                name?.let {
                    toast('"' + it + '"' + " " + getString(R.string.msg_device_disconnected))
                }
            }
        })
    }


    override fun reConnectBridge() {
        releaseResource()
        mConnected = false
        mMeshHandler.postDelayed({ connectBridge() }, 250)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConfig.REQUEST_BT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                initSppService()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val dialog = PermissionPromptDialogFragment()
                dialog.setConfirmButtonClickListener(object : PermissionPromptDialogFragment.ConfirmButtonClickListener {
                    override fun onConfirm() {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBtIntent, AppConfig.REQUEST_BT_CODE)
                    }
                })
                dialog.show(supportFragmentManager, "PermissionPromptDialogFragment")
            }
        }
    }

    private val mScanCallBack = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        mService?.processMeshAdvert(device, scanRecord, rssi)
        if (!TextUtils.isEmpty(device.name) && !addressToNameMap.containsKey(device.address)) {
            if (TextUtils.equals("Linkuphome M1", device.name)) {
                sppStateListener?.newAppearance(device.name, device.address)
            } else {
                addressToNameMap[device.address] = device.name
            }
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
                    parentActivity?.startActivityForResult(enableBtIntent, AppConfig.REQUEST_BT_CODE)
                }
                MeshService.MESSAGE_LE_CONNECTED -> {
                    val address = msg.data.getString(MeshService.EXTRA_DEVICE_ADDRESS)
                    address?.let {
                        if (parentActivity != null) {
                            parentActivity.mConnectedDevices.add(it)
                            val name = parentActivity.addressToNameMap[it]
                            if (!parentActivity.mConnected && name != null && !TextUtils.isEmpty(name)) {
                                parentActivity.runOnUiThread {
                                    parentActivity.onConnected(name)
                                }
                            }
                        }
                    }
                }
                MeshService.MESSAGE_LE_DISCONNECTED -> {
                    val numConnections = msg.data.getInt(MeshService.EXTRA_NUM_CONNECTIONS)
                    val address = msg.data.getString(MeshService.EXTRA_DEVICE_ADDRESS)
                    if (numConnections == 0) {
                        address?.let {
                            if (parentActivity != null) {
                                parentActivity.mConnectedDevices.remove(it)
                                val name = parentActivity.addressToNameMap[it]
                                if (parentActivity.mConnected && name != null && !TextUtils.isEmpty(name)) {
                                    parentActivity.runOnUiThread {
                                        parentActivity.onDisConnected(name)
                                    }
                                }
                            }
                        }
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
                MeshService.MESSAGE_TIMEOUT -> {
                    val expectedMsg = msg.data.getInt(MeshService.EXTRA_EXPECTED_MESSAGE)
                    val id = if (msg.data.containsKey(MeshService.EXTRA_UUIDHASH_31)) {
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


    private fun onMessageTimeout(expectedMessage: Int, id: Int, meshRequestId: Int) {
        when (expectedMessage) {
            MeshService.MESSAGE_DEVICE_ASSOCIATED, MeshService.MESSAGE_CONFIG_MODELS -> {
                if (meshAssListener != null) {
                    meshAssListener?.deviceAssociated(-1, getString(R.string.msg_device_connect_failed))
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
        textView.text = '"' + name + '"' + " " + getString(R.string.msg_device_connected)
        Crouton.make(this, textView).show()
        mMeshHandler.postDelayed({ mViewModel.setBridgeState(mConnected) }, 550)
    }

    private fun onDisConnected(name: String) {
        mConnected = false
        mViewModel.setBridgeState(mConnected)
        val textView = TextView(this)
        textView.width = getScreenW()
        textView.setPadding(0, dip2px(36f), 0, dip2px(18f))
        textView.gravity = Gravity.CENTER
        textView.setTextColor(resources.getColor(android.R.color.white))
        textView.setBackgroundResource(R.color.colorPrimaryDark)
        textView.text = '"' + name + '"' + " " + getString(R.string.msg_device_disconnected)
        Crouton.make(this, textView).show()
    }


    private fun showCustomToast(name: String) {
        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.BOTTOM, 0, dip2px(64f))
        toast.duration = Toast.LENGTH_LONG
        val textView = TextView(this)
        textView.gravity = Gravity.CENTER
        textView.setPadding(dip2px(24f), dip2px(10f), dip2px(24f), dip2px(10f))
        textView.setTextColor(resources.getColor(android.R.color.black))
        textView.setBackgroundResource(R.drawable.bg_custom_toast)
        textView.text = name
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._18ssp))
        toast.view = textView
        toast.show()
    }

    override fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener?) {
        meshAssListener = if (enabled && listener != null) listener else null
        try {
            mService?.setDeviceDiscoveryFilterEnabled(enabled)
        } catch (e: Exception) {
            Log.d("LinkupHome", "you should firstly connect to bridge!")
        }
    }

    override fun discoverDevices(enabled: Boolean, listener: SppStateListener?) {
        sppStateListener = if (enabled) listener else null
        try {
            mService?.setDeviceDiscoveryFilterEnabled(enabled)
        } catch (e: Exception) {
            Log.d("LinkupHome", "you should firstly connect to bridge!")
        }
    }

    override fun getEnvironmentalIndicators(deviceAddress: String?, listener: EmtValueListener?) {
        this.emtValueListener = listener
        deviceAddress?.let { BluetoothSPP.getInstance().send(it, decodeHex("BF01D101CD04C10207EFBD16".toUpperCase().toCharArray()), false) }
    }

    override fun associateDevice(uuidHash: Int, shortCode: String?) {
        if (shortCode == null) {
            mService?.associateDevice(uuidHash, 0, false)
        }
    }

    override fun associateDevice(macAddress: String) {
        BluetoothSPP.getInstance()?.connect(macAddress)
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

}
