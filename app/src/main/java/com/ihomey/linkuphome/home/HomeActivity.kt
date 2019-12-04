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
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.RemoveDeviceVo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.ConnectDeviceFragment
import com.ihomey.linkuphome.device.ConnectM1DeviceFragment
import com.ihomey.linkuphome.devicecontrol.controller.impl.M1Controller
import com.ihomey.linkuphome.dialog.PermissionPromptDialogFragment
import com.ihomey.linkuphome.listener.*
import com.ihomey.linkuphome.listener.BatteryValueListener
import com.ihomey.linkuphome.sigmesh.CSRMeshServiceManager
import com.ihomey.linkuphome.sigmesh.MeshStateListener
import com.ihomey.linkuphome.sigmesh.SigMeshServiceManager
import com.ihomey.linkuphome.spp.BluetoothSPP
import com.pairlink.sigmesh.lib.*
import de.keyboardsurfer.android.widget.crouton.Crouton
import kotlinx.android.synthetic.main.home_activity.*
import org.spongycastle.util.encoders.Hex
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess


class HomeActivity : BaseActivity(), BridgeListener, OnLanguageListener,  ConnectM1DeviceFragment.DevicesStateListener, MeshStateListener {


    private lateinit var mViewModel: HomeActivityViewModel

    private var mCurrentZone: Zone? = null


    private var isBackground = false

    private var sppStateListener: SppStateListener? = null
    private var mBatteryListener: BatteryValueListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucentStatus()
        setContentView(R.layout.home_activity)
        initNavController()
//        BluetoothSPP.getInstance().initialize(applicationContext)
//        initSppService()
//        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
//        if (BluetoothAdapter.getDefaultAdapter().isEnabled)

//            bindService(Intent(this, PlSigMeshService::class.java), mPlSigMeshServiceConnection, Context.BIND_AUTO_CREATE)

        CSRMeshServiceManager.getInstance().bind(this)
        SigMeshServiceManager.getInstance().bind(this)
        CSRMeshServiceManager.getInstance().setMeshStateListener(this)
        SigMeshServiceManager.getInstance().setMeshStateListener(this)
        initViewModel()
    }


    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                mCurrentZone=it.data
                it.data?.let {
                    CSRMeshServiceManager.getInstance().initService(it)
                    createPlSigMeshNet()
                    SigMeshServiceManager.getInstance().initService(it)
                }

//                mPlSigMeshService?.let { it0 ->
//                    if (TextUtils.isEmpty(it.data?.meshInfo)) {
//                        Log.d("aa", "ccccc---" + it)
//                        createPlSigMeshNet()
//                        if (!TextUtils.equals(it.data?.meshInfo, it0.getJsonStrMeshNet(0).encodeBase64())) {
//                            mViewModel.uploadMeshInfo(getIMEI(), it.data?.id, it.data?.name, it0.getJsonStrMeshNet(0).encodeBase64()).observe(this, Observer<Resource<Zone>> {
//                                if (it?.status != Status.LOADING) initMeshNet()
//                            })
//                        }
//                    } else {
//                        if (!TextUtils.equals(it.data?.meshInfo, it0.getJsonStrMeshNet(0).encodeBase64())) {
//                            Log.d("aa", "aaaa11---" + it.data?.meshInfo)
//                            Log.d("aa", "aaaa222---" + it0.getJsonStrMeshNet(0).encodeBase64())
//                            it.data?.meshInfo?.let { it0.updateJsonStrMeshNet(it.decodeBase64(), ArrayList(0)) }
//                            initMeshNet()
//                            Log.d("aa", "bbbb---" + it0.meshList.size + "---" + PlSigMeshService.getInstance().getJsonStrMeshNet(0))
//                        }
//                    }
//                }
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
//            unbindService(mServiceConnection)
//            unbindService(mPlSigMeshServiceConnection)
        } catch (e: Exception) {
            Log.d("LinkupHome", "oh,some error happen!")
        }
    }

    override fun onBackPressed() {
        val homeFragment = nav_host_home.childFragmentManager.fragments[0]
        if (!handleBackPress(homeFragment)) {
            finish()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    override fun onLanguageChange(languageIndex: Int) {
        showLoadingView()
        mViewModel.setRemoveDeviceFlag(false)
        val desLanguage = AppConfig.LANGUAGE[languageIndex]
        val currentLanguage = LocaleHelper.getLanguage(this)
        if (!TextUtils.equals(currentLanguage, desLanguage)) {
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
//        mPlSigMeshService?.proxyExit()
//        mService?.setDeviceDiscoveryFilterEnabled(false)
//        if (mConnected) mService?.disconnectBridge()
//        mService?.setHandler(null)
//        mMeshHandler.removeCallbacksAndMessages(null)
    }

//    private val mServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
//            mService = (rawBinder as MeshService.LocalBinder).service
//            connectBridge()
//        }
//
//        override fun onServiceDisconnected(classname: ComponentName) {
//            mService = null
//        }
//    }

//    private fun initMeshNet() {
//        Log.d("aa", "222222")
//        mPlSigMeshNet = PlSigMeshService.getInstance().chooseMeshNet(0)
//        mPlSigMeshService?.scanDevice(true, Util.SCAN_TYPE_PROXY)
//        PlSigMeshService.getInstance().registerProxyCb(mSigMeshProxyCB)
//        mPlSigMeshService?.proxyJoin()
//    }
//
//    private val mPlSigMeshServiceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, rawBinder: IBinder) {
//            Log.d("aa","11111")
//            mPlSigMeshService = (rawBinder as PlSigMeshService.LocalBinder).service
//            mPlSigMeshService?.let {
//                it.init(this@HomeActivity, Util.DBG_LEVEL_WARN, Util.DBG_LEVEL_WARN)
//            }
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            Log.d("aa","222222")
//            mPlSigMeshService = null
//        }
//    }


    override fun connectBridge() {
//        mService?.setHandler(mMeshHandler)
//        mService?.setLeScanCallback(mScanCallBack)
//        mService?.setMeshListeningMode(true, true)
//        mService?.autoConnect(1, 10000, 100, 0)
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

        mMeshHandler.postDelayed({ connectBridge() }, 250)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConfig.REQUEST_BT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
//                bindService(Intent(this, PlSigMeshService::class.java), mPlSigMeshServiceConnection, Context.BIND_AUTO_CREATE)
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
        Log.d("aa",device.name+"---"+device.address)
//        mService?.processMeshAdvert(device, scanRecord, rssi)
//        if (!TextUtils.isEmpty(device.name) && !addressToNameMap.containsKey(device.address)) {
//            if (TextUtils.equals("Linkuphome M1", device.name)) {
//                sppStateListener?.newAppearance(device.name, device.address)
//            } else {
//                addressToNameMap[device.address] = device.name
//            }
//        }
    }

    private val mMeshHandler = MeshHandler(this)

    private class MeshHandler(activity: HomeActivity) : Handler() {
        private val mActivity: WeakReference<HomeActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val parentActivity = mActivity.get()
            when (msg.what) {
                MeshService.MESSAGE_REQUEST_BT -> {
//                    if (parentActivity?.mPlSigMeshService != null) parentActivity.unbindService(parentActivity.mPlSigMeshServiceConnection)
                    BluetoothSPP.getInstance()?.removeOnDataReceivedListener(parentActivity?.mOnDataReceivedListener)
                    BluetoothSPP.getInstance()?.stopService()
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    parentActivity?.startActivityForResult(enableBtIntent, AppConfig.REQUEST_BT_CODE)
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


//    private val mSigMeshProxyCB = object : PlSigMeshProxyCallback() {

//

//
//        override fun onConfigComplete(result: Int, config_node: MeshNetInfo.MeshNodeInfo, mesh_net: MeshNetInfo?) {
//            Log.d("aa", "onConfigComplete $result---" + config_node + "----" + mesh_net + "---" + config_node.primary_addr)
//            mPlSigMeshNet = mesh_net
//            runOnUiThread {
//                meshAssListener?.associationProgress(99)
//                meshAssListener?.deviceAssociated(config_node.primary_addr.toInt(), 0, config_node.uuid)
//            }
//        }
//
//
//        override fun onNodeResetStatus(src: Short) {
//            super.onNodeResetStatus(src)
//            Log.d("aa", "onNodeResetStatus")
//            mPlSigMeshService?.delMeshNode(src)
//            runOnUiThread {
//                deleteSigMeshDevice()
//            }
//        }
//
//    }

    private fun deleteSigMeshDevice() {
        mCurrentZone?.let {
            mViewModel.uploadMeshInfo(getIMEI(), it.id, it.name, PlSigMeshService.getInstance().getJsonStrMeshNet(0).encodeBase64()).observe(this, Observer<Resource<Zone>> {})
        }
    }

//    private val mSigMeshProvisionCB = object : PlSigMeshProvisionCallback() {
//        override fun onDeviceFoundUnprovisioned(device: BluetoothDevice, rssi: Int, uuid: String) {
//            meshAssListener?.onDeviceFound(uuid, device.address, device.name)
//            Log.d("aa", "onDeviceFoundUnprovisioned---" + device.address + ", " + device.name + ", uuid:" + uuid)
//        }
//
//        override fun onProvisionComplete(result: Int, provision_node: MeshNetInfo.MeshNodeInfo?, mesh_net: MeshNetInfo?) {
//            mPlSigMeshNet = mesh_net
//            if (Util.CONFIG_MODE_PROVISION_CONFIG_ONE_BY_ONE.toInt() == mPlSigMeshService?._config_mode && result == 0) {
//                runOnUiThread {
//                    meshAssListener?.associationProgress(50)
//                }
//            }
//        }
//    }


    override fun onDeviceDisConnected(name: String) {
        val textView = TextView(this)
        textView.width = getScreenW()
        textView.setPadding(0, dip2px(36f), 0, dip2px(18f))
        textView.gravity = Gravity.CENTER
        textView.setTextColor(resources.getColor(android.R.color.white))
        textView.setBackgroundResource(R.color.colorPrimaryDark)
        textView.text = '"' + name + '"' + " " + getString(R.string.msg_device_disconnected)
        Crouton.make(this, textView).show()
    }

    override fun onDeviceConnected(name: String) {
        val textView = TextView(this)
        textView.width = getScreenW()
        textView.setPadding(0, dip2px(36f), 0, dip2px(18f))
        textView.gravity = Gravity.CENTER
        textView.setTextColor(resources.getColor(android.R.color.white))
        textView.setBackgroundResource(R.color.bridge_connected_msg_bg_color)
        textView.text = '"' + name + '"' + " " + getString(R.string.msg_device_connected)
        Crouton.make(this, textView).show()
    }


//    override fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener?) {
////        meshAssListener = if (enabled && listener != null) listener else null
//        try {
////            mService?.setDeviceDiscoveryFilterEnabled(enabled)
//            if (enabled) {
////                CSRMeshServiceManager.getInstance().startScan()
////                SigMeshServiceManager.getInstance().startScan()
////                mPlSigMeshService?.scanDevice(false, Util.SCAN_TYPE_PROXY)
////                mPlSigMeshService?.proxyExit()
////                mPlSigMeshService?.registerProvisionCb(mSigMeshProvisionCB)
////                mPlSigMeshService?.scanDevice(true, Util.SCAN_TYPE_PROVISION)
//            }else{
////                CSRMeshServiceManager.getInstance().stopScan()
////                SigMeshServiceManager.getInstance().stopScan()
////                mPlSigMeshService?.scanDevice(false, Util.SCAN_TYPE_PROVISION)
////                mPlSigMeshService?.scanDevice(true, Util.SCAN_TYPE_PROXY)
////                mPlSigMeshService?.proxyJoin()
//            }
//
//        } catch (e: Exception) {
//            Log.d("LinkupHome", "you should firstly connect to bridge!")
//        }
//    }

    override fun discoverDevices(enabled: Boolean, listener: SppStateListener?) {
        sppStateListener = if (enabled) listener else null
//        try {
//            mService?.setDeviceDiscoveryFilterEnabled(enabled)
//        } catch (e: Exception) {
//            Log.d("LinkupHome", "you should firstly connect to bridge!")
//        }
    }




//    override fun getBatteryState(deviceId: Int, batteryValueListener: BatteryValueListener) {
//        mBatteryListener = batteryValueListener
//        DataModelApi.sendData(deviceId, decodeHex("B600B6".toCharArray()), false)
//    }

}
