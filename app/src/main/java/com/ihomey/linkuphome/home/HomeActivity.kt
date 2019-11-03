package com.ihomey.linkuphome.home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import com.ihomey.linkuphome.controller.M1Controller
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
import com.pairlink.sigmesh.lib.*
import de.keyboardsurfer.android.widget.crouton.Crouton
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.m1_control_setting_fragment.*
import org.spongycastle.util.encoders.Hex
import java.lang.ref.WeakReference
import java.util.*


class HomeActivity : BaseActivity(), BridgeListener, OnLanguageListener, MeshServiceStateListener, ConnectDeviceFragment.DevicesStateListener, ConnectM1DeviceFragment.DevicesStateListener {

    private val REMOVE_ACK_WAIT_TIME_MS = 4 * 1000L

    private lateinit var mViewModel: HomeActivityViewModel

    private var mService: MeshService? = null
    var mPlSigMeshService: PlSigMeshService? = null
    private var mPlSigMeshNet: MeshNetInfo? = null

    private var isBackground = false
    private var mConnected = false
    private val mDeviceIdToUuidHash = SparseIntArray()
    private val mConnectedDevices = HashSet<String>()
    private val addressToNameMap = ArrayMap<String, String>()

    private var meshAssListener: DeviceAssociateListener? = null
    private var sppStateListener: SppStateListener? = null
    private var mBatteryListener: BatteryValueListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucentStatus()
        setContentView(R.layout.home_activity)
        initNavController()
        BluetoothSPP.getInstance().initialize(applicationContext)
        initSppService()
        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
        if (BluetoothAdapter.getDefaultAdapter().isEnabled) bindService(Intent(this, PlSigMeshService::class.java), mPlSigMeshServiceConnection, Context.BIND_AUTO_CREATE)
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
        mViewModel.mRemoveSigmeshDeviceVo.observe(this, Observer<RemoveDeviceVo> {
            if (it != null) {
                mPlSigMeshService?.resetNode(it.deviceInstructId.toShort())
                mMeshHandler.postDelayed({
                    mPlSigMeshService?.delMeshNode(it.deviceInstructId.toShort())
                    it.deviceRemoveListener.onDeviceRemoved(it.deviceId, it.deviceInstructId, true)
                }, 2000)
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
            unbindService(mPlSigMeshServiceConnection)
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
        showLoadingView()
        mViewModel.setRemoveDeviceFlag(false)
        val desLanguage = AppConfig.LANGUAGE[languageIndex]
        val currentLanguage = LocaleHelper.getLanguage(this)
        if (!TextUtils.equals(currentLanguage, desLanguage)) {
            mViewModel.setBridgeState(false)
            LocaleHelper.setLocale(this, desLanguage)
            releaseResource()
            Handler().postDelayed({
                recreate()
                hideLoadingView()
            }, 1500)
        }
    }

    override fun onStop() {
        super.onStop()
        isBackground = true
    }

    override fun onResume() {
        super.onResume()
        isBackground = false
    }

    private fun releaseResource() {
        Crouton.cancelAllCroutons()
        BluetoothSPP.getInstance()?.release()
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


    private val mPlSigMeshServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, rawBinder: IBinder) {
            mPlSigMeshService = (rawBinder as PlSigMeshService.LocalBinder).service
            mPlSigMeshService?.let {
                it.init(this@HomeActivity, true, Util.DBG_LEVEL_WARN)
                mPlSigMeshNet = it.chooseMeshNet(0)
                mPlSigMeshService?.scanDevice(true, Util.SCAN_TYPE_PROXY)
                it.registerProxyCb(mSigMeshProxyCB)
                mPlSigMeshService?.proxyJoin()
//                if (mPlSigMeshNet == null) {
                    Log.d("aa", "create mesh net")
                    createPlSigMeshNet()
                    it.chooseMeshNet(0)
//                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mPlSigMeshService = null
        }
    }

    fun gen(): Int {
        val r = Random(System.currentTimeMillis())
        return 10000000 + r.nextInt(20000000)
    }

    private fun createPlSigMeshNet() {
        val homeId_tmp = "" + gen()
        val homeId = homeId_tmp.toByteArray()
        val netkey = ByteArray(16)
        val appkey = ByteArray(16)
        for (i in 0..15) {
            netkey[i] = 0
            appkey[i] = 0
        }
        System.arraycopy(homeId, 0, netkey, 0, 4)
        System.arraycopy(homeId, 4, appkey, 0, 4)
        val mesh_net = MeshNetInfo()
        mesh_net.name = homeId_tmp
        mesh_net.node_next_addr = 2
        mesh_net.admin_next_addr = 0x7ffe
        mesh_net.mesh_version = 0
        mesh_net.gateway = false
        mesh_net.netkey = Util.byte2HexStr(netkey)
        mesh_net.appkey = Util.byte2HexStr(appkey)
        mesh_net.iv_index = 0
        mesh_net.seq = 1
        mesh_net.nodes.clear()
        mesh_net.current_admin = PlSigMeshService.getInstance().current_admin
        mesh_net.admin_nodes.clear()

        val admin_node = MeshNetInfo.AdminNodeInfo()
        admin_node.uuid = mesh_net.current_admin
        admin_node.name = "user"
        admin_node.addr = 0x7fff
        mesh_net.admin_nodes.add(admin_node)
        PlSigMeshService.getInstance().addMeshNet(mesh_net)
    }

    override fun connectBridge() {
        mService?.setHandler(mMeshHandler)
        mService?.setLeScanCallback(mScanCallBack)
        mService?.setMeshListeningMode(true, true)
        mService?.autoConnect(1, 10000, 100, 0)
    }

    private fun initSppService() {
        BluetoothSPP.getInstance()?.startService()
        BluetoothSPP.getInstance()?.addOnDataReceivedListener(mOnDataReceivedListener)
        BluetoothSPP.getInstance()?.setBluetoothStateListener(object : BluetoothSPP.BluetoothStateListener {
            override fun onDeviceConnected(name: String?, address: String?) {
                val controller = M1Controller()
                Handler().postDelayed({ controller.syncTime(address) }, 250)
                if (isBackground) {
                    val lastPushTime by PreferenceHelper("lastPushTime", 0L)
                    val currentTimeMillis = System.currentTimeMillis()
                    if (lastPushTime == 0L || currentTimeMillis - lastPushTime > 6 * 60 * 60 * 1000) {
                        Handler().postDelayed({ controller.getEnvironmentalValue(address) }, 5000)
                    }
                } else {
                    toast(R.string.msg_m1_connected, Toast.LENGTH_SHORT)
                }
            }

            override fun onServerStartListen() {

            }

            override fun onDeviceDisConnected(name: String?, address: String?) {
                if (!isBackground) toast(R.string.msg_m1_disconnected, Toast.LENGTH_SHORT)
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
                bindService(Intent(this, PlSigMeshService::class.java), mPlSigMeshServiceConnection, Context.BIND_AUTO_CREATE)
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

    private val mOnDataReceivedListener = BluetoothSPP.OnDataReceivedListener { data, message, address ->
        val receiveDataStr = Hex.toHexString(data).toUpperCase()
        if (receiveDataStr.startsWith("FE01D101DA0004C1F")) {
            val sensorType = if (receiveDataStr.startsWith("FE01D101DA0004C1F2F2F2")) 1 else 0
            mViewModel.updateM1Version(address, sensorType)
        } else if (receiveDataStr.startsWith("FE01D101DA000BC107") && isBackground) {
            val pm25Value = Integer.parseInt(receiveDataStr.substring(18, 20), 16) * 256 + Integer.parseInt(receiveDataStr.substring(20, 22), 16)
            val hchoValue = Integer.parseInt(receiveDataStr.substring(22, 24), 16) * 256 + Integer.parseInt(receiveDataStr.substring(24, 26), 16)
            val vocValue = Integer.parseInt(receiveDataStr.substring(26, 28), 16)
            NotifyManager.getInstance(applicationContext).showNotify(getString(R.string.title_welcom_home), getString(R.string.title_pm25) + "：" + pm25Value + "，" + getString(R.string.title_hcho) + "：" + hchoValue + " μg/m³，" + getString(R.string.title_voc) + "：" + getVOCLevel(vocValue))
            var lastPushTime by PreferenceHelper("lastPushTime", 0L)
            lastPushTime = System.currentTimeMillis()
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
                    if(parentActivity?.mPlSigMeshService!=null) parentActivity.unbindService(parentActivity.mPlSigMeshServiceConnection)
                    BluetoothSPP.getInstance()?.removeOnDataReceivedListener(parentActivity?.mOnDataReceivedListener)
                    BluetoothSPP.getInstance()?.stopService()
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
                    val address = msg.data.getString(MeshService.EXTRA_DEVICE_ADDRESS)
                    val shortName = msg.data.getString(MeshService.EXTRA_SHORTNAME)
                    val uuidHash = msg.data.getInt(MeshService.EXTRA_UUIDHASH_31)
                    parentActivity?.meshAssListener?.onDeviceFound(uuidHash.toString(), address, shortName)
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
                        if (uuidHash != 0) {
                            parentActivity?.mDeviceIdToUuidHash?.removeAt(parentActivity.mDeviceIdToUuidHash.indexOfKey(deviceId))
                            parentActivity?.meshAssListener?.deviceAssociated(deviceId, uuidHash!!, "")
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


    private val mSigMeshProxyCB = object : PlSigMeshProxyCallback() {
        override fun onCTLStatus(src: Short, lightness: Int, temperature: Int) {
            super.onCTLStatus(src, lightness, temperature)
        }

        override fun onSceneStoreChanged(src: Short, status: Int, current: Short, scene_list: MutableList<Short>?) {
            super.onSceneStoreChanged(src, status, current, scene_list)
        }

        override fun onMeshStatus(status: Int, addr: String?) {
            Log.d("aa", "onMeshStatus---" + status)
            when (status) {
                Util.PL_MESH_READY -> {

                }
                Util.PL_MESH_JOIN_FAIL -> {

                }
                Util.PL_MESH_EXIT -> {

                }
            }
        }

        override fun onVendorUartData(src: Short, data: ByteArray?) {
            super.onVendorUartData(src, data)
        }

        override fun onConfigComplete(result: Int, config_node: MeshNetInfo.MeshNodeInfo, mesh_net: MeshNetInfo?) {
            Log.d("aa", "onConfigComplete $result---" + config_node + "----" + mesh_net + "---" + config_node.primary_addr)
            mPlSigMeshNet = mesh_net
            runOnUiThread {
                meshAssListener?.associationProgress(99)
                meshAssListener?.deviceAssociated(config_node.primary_addr.toInt(), 0, config_node.uuid)
            }
        }

        override fun onTestCounterGet(src: Short, coutner: Int) {
            super.onTestCounterGet(src, coutner)
        }

        override fun onHSLStatus(src: Short, lightness: Int, hue: Int, saturation: Int) {
            super.onHSLStatus(src, lightness, hue, saturation)
        }

        override fun onDeviceFoundProxy(device: BluetoothDevice?, rssi: Int, dev_list_size: Int) {
            super.onDeviceFoundProxy(device, rssi, dev_list_size)
        }

        override fun onSubsChanged(src: Short, status: Int, ele_addr: Short, subs_addr: Short, vendor: Short, model: Short) {
            super.onSubsChanged(src, status, ele_addr, subs_addr, vendor, model)
        }

        override fun onDataReceived(src: Short, op: Int, data: ByteArray?, len: Int) {
            super.onDataReceived(src, op, data, len)
        }

        override fun onNodeResetStatus(src: Short) {
            super.onNodeResetStatus(src)
            Log.d("aa", "onNodeResetStatus")
//            mPlSigMeshService?.delMeshNode(src)
        }

        override fun onOnOffChanged(src: Short, onoff: Boolean) {
            super.onOnOffChanged(src, onoff)
        }

        override fun onPubsChanged(src: Short, status: Int, ele_addr: Short, pubs_addr: Short, vendor: Short, model: Short) {
            super.onPubsChanged(src, status, ele_addr, pubs_addr, vendor, model)
        }

        override fun onVendorBtFuncStatus(src: Short, status: Int, data: ByteArray?) {
            super.onVendorBtFuncStatus(src, status, data)
        }

        override fun onPowerLevelChanged(src: Short, level: Short) {
            super.onPowerLevelChanged(src, level)
        }

        override fun onVersionGet(src: Short, ver: String?) {
            super.onVersionGet(src, ver)
        }

        override fun onJiechangConfigStatus(src: Short, data: ByteArray?, len: Int) {
            super.onJiechangConfigStatus(src, data, len)
        }

        override fun onReliablePacketTimeout(op: String?, dst: Short) {
            super.onReliablePacketTimeout(op, dst)
        }

        override fun onLevelChanged(src: Short, level: Short) {
            super.onLevelChanged(src, level)
        }

        override fun onSceneChanged(src: Short, status: Int, current: Short) {
            super.onSceneChanged(src, status, current)
        }
    }

    private val mSigMeshProvisionCB = object : PlSigMeshProvisionCallback() {
        override fun onDeviceFoundUnprovisioned(device: BluetoothDevice, rssi: Int, uuid: String) {
            meshAssListener?.onDeviceFound(uuid, device.address, device.name)
            Log.d("aa", "onDeviceFoundUnprovisioned---" + device.address + ", " + device.name + ", uuid:" + uuid)
        }

        override fun onProvisionComplete(result: Int, provision_node: MeshNetInfo.MeshNodeInfo?, mesh_net: MeshNetInfo?) {
            mPlSigMeshNet = mesh_net
            if (Util.CONFIG_MODE_PROVISION_CONFIG_ONE_BY_ONE.toInt() == mPlSigMeshService?._config_mode && result == 0) {
                runOnUiThread {
                    meshAssListener?.associationProgress(50)
                }
            }
        }

        override fun onAdvProvisionComplete(mesh_net: MeshNetInfo?) {
            Log.d("aa", "onAdvProvisionComplete $mesh_net")
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

    override fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener?) {
        meshAssListener = if (enabled && listener != null) listener else null
        try {
            mService?.setDeviceDiscoveryFilterEnabled(enabled)
            if (enabled) {
                mPlSigMeshService?.scanDevice(false, Util.SCAN_TYPE_PROXY)
                mPlSigMeshService?.proxyExit()
                mPlSigMeshService?.registerProvisionCb(mSigMeshProvisionCB)
            }
            mPlSigMeshService?.scanDevice(enabled, Util.SCAN_TYPE_PROVISION)
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


    override fun associateDevice(uuidHash: String, macAddress: String?) {
        if (TextUtils.isEmpty(macAddress)) {
            mService?.associateDevice(uuidHash.toInt(), 0, false)
        } else {
            mPlSigMeshService?.scanDevice(false, Util.SCAN_TYPE_PROVISION)
            mPlSigMeshService?.registerProvisionCb(mSigMeshProvisionCB)
            val unProvisionedDev = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress)
            val info = Util.hexStringToBytes(uuidHash)
            val ele_num = info[15]
            mPlSigMeshService?.startProvision(unProvisionedDev, ele_num)
        }
    }


    override fun isMeshServiceConnected(): Boolean {
        return true
    }

    override fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener) {
        mBatteryListener = batteryValueListener
        if (mConnected) {
            DataModelApi.sendData(deviceId, decodeHex("B600B6".toCharArray()), false)
        }
    }

}
