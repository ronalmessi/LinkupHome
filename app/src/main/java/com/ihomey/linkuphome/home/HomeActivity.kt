package com.ihomey.linkuphome.home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.AppConfig.Companion.REQUEST_CODE_OPEN_GPS
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.controller.impl.M1Controller
import com.ihomey.linkuphome.dialog.PermissionPromptDialogFragment
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.OnLanguageListener
import com.ihomey.linkuphome.protocol.csrmesh.CSRMeshServiceManager
import com.ihomey.linkuphome.protocol.sigmesh.MeshInfoListener
import com.ihomey.linkuphome.protocol.sigmesh.MeshStateListener
import com.ihomey.linkuphome.protocol.sigmesh.SigMeshServiceManager
import com.ihomey.linkuphome.protocol.spp.BluetoothSPP
import com.pairlink.sigmesh.lib.PlSigMeshService
import de.keyboardsurfer.android.widget.crouton.Crouton
import kotlinx.android.synthetic.main.home_activity.*
import org.spongycastle.util.encoders.Hex
import kotlin.system.exitProcess


class HomeActivity : BaseActivity(), BridgeListener, OnLanguageListener, MeshStateListener, MeshInfoListener {

    private lateinit var mViewModel: HomeActivityViewModel

    private var mCurrentZone: Zone? = null

    private var isBackground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        initNavController()
        BluetoothSPP.getInstance().initialize(applicationContext)
        initSppService()


        CSRMeshServiceManager.getInstance().bind(this)
        CSRMeshServiceManager.getInstance().setMeshStateListener(this)


        SigMeshServiceManager.getInstance().bind(this)
        SigMeshServiceManager.getInstance().setMeshInfoListener(this)
        SigMeshServiceManager.getInstance().setMeshStateListener(this)

        initViewModel()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                mCurrentZone = it.data
                it.data?.let {
                    Log.d("aa", "hahahahhahaa---"+it.nextDeviceIndex)
                    CSRMeshServiceManager.getInstance().initService(it)
                    if(!SigMeshServiceManager.getInstance().isInited){
                        SigMeshServiceManager.getInstance().plSigMeshService?.let {it0->
                            if(TextUtils.isEmpty(it.meshInfo)){
                                SigMeshServiceManager.getInstance().initService(it)
                                Log.d("aa", "ccccc---"+it0.getJsonStrMeshNet(0).encodeBase64())
                                onMeshInfoChanged()
                            }else {
                                Log.d("aa", "aaaa11---" + it.meshInfo?.decodeBase64())
                                Log.d("aa", "aaaa222---" + it0.getJsonStrMeshNet(0))
                                it.meshInfo?.let { it0.updateJsonStrMeshNet(it.decodeBase64(), ArrayList(0)) }
                                SigMeshServiceManager.getInstance().initService(it)
                            }
                        }
                    }
                }
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
        releaseResource()
    }

    private fun releaseResource() {
        try {
            Crouton.cancelAllCroutons()
            BluetoothSPP.getInstance()?.release()
            CSRMeshServiceManager.getInstance().unBind(this)
            SigMeshServiceManager.getInstance().unBind(this)
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
        if (!checkGPSIsOpen()) {
            showOpenGPSDialog()
        }
    }


    private fun showOpenGPSDialog() {
        val dialog = PermissionPromptDialogFragment().newInstance(getString(R.string.msg_notes), "需要开启定位服务以扫描周围设备", "前往开启")
        dialog.setConfirmButtonClickListener(object : PermissionPromptDialogFragment.ConfirmButtonClickListener {
            override fun onConfirm() {
                val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, REQUEST_CODE_OPEN_GPS)
            }
        })
        dialog.show(supportFragmentManager, "PermissionPromptDialogFragment")
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
        Handler().postDelayed({ }, 250)
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

    override fun onDeviceStateChanged(name: String, isConnected: Boolean) {
        showCrouton('"' + name + '"' + " " + getString(if (isConnected) R.string.msg_device_connected else R.string.msg_device_disconnected), if (isConnected) R.color.bridge_connected_msg_bg_color else R.color.colorPrimaryDark)
    }


    override fun onMeshInfoChanged() {
        mCurrentZone?.let {
            mViewModel.uploadMeshInfo(getIMEI(), it.id, it.name, PlSigMeshService.getInstance().getJsonStrMeshNet(0).encodeBase64()).observe(this, Observer<Resource<Zone>> {})
        }
    }

}
