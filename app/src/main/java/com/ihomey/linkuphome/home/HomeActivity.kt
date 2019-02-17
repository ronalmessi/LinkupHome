package com.ihomey.linkuphome.home

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.util.SparseIntArray
import android.view.Gravity
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.csr.mesh.ConfigModelApi
import com.csr.mesh.MeshService
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.SingleDevice
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.MeshDeviceListFragment
import com.ihomey.linkuphome.device1.ConnectDeviceFragment
import com.ihomey.linkuphome.device1.DevicesFragment
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listeners.BatteryValueListener
import com.ihomey.linkuphome.listeners.DeviceAssociateListener
import com.ihomey.linkuphome.listeners.DeviceRemoveListener
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import de.keyboardsurfer.android.widget.crouton.Crouton
import java.lang.ref.WeakReference
import java.util.HashSet


class HomeActivity : BaseActivity(), BridgeListener, MeshServiceStateListener, ConnectDeviceFragment.DevicesStateListener, DevicesFragment.DevicesStateListener {

    private val REMOVE_ACK_WAIT_TIME_MS = 10 * 1000L

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucentStatus()
        setContentView(R.layout.activity_home)
        scheduleScreen()
        mViewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun scheduleScreen() {
        val hasZone by PreferenceHelper("hasZone", false)
        val finalHost = NavHostFragment.create(if (hasZone) R.navigation.nav_zone_init_3 else R.navigation.nav_zone_init_1)
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
        mViewModel.getGlobalSetting()?.observe(this, Observer<Resource<LampCategory>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                mService?.setNextDeviceId(it.data.nextDeviceIndex)
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

    override fun removeDevice(deviceId: Int,deviceHash:Int, listener: DeviceRemoveListener) {
        mRemovedUuidHash =deviceHash
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

    }

}
