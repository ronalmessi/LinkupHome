package com.ihomey.linkuphome.share

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.ActivityShareBinding
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Created by dongcaizheng on 2017/12/21.
 */
class ShareActivity : BaseActivity() {


    private lateinit var mViewDataBinding: ActivityShareBinding
    private lateinit var mViewModel: ShareViewModel
    private lateinit var generatingDialog: ShareCodeGeneraeFragment
    private var lampCategoryType = -1
    private var bitmap: Bitmap? = null
    private var settings: List<LampCategory>? = null
    private var deviceModels: List<DeviceModel>? = null
    private var groups: List<GroupDevice>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ShareViewModel::class.java)
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_share)
        mViewDataBinding.isChinese = TextUtils.equals("zh", language)
        mViewDataBinding.handlers = ShareHandler()
        generatingDialog = ShareCodeGeneraeFragment()

        lampCategoryType = intent.getIntExtra("lampCategoryType", -1)

        mViewModel.getDeviceResults().observe(this, Observer<Resource<List<DeviceModel>>> {
            if (it?.status == Status.SUCCESS) {
                deviceModels = it.data
            }
            mViewModel.loadGroups(lampCategoryType)
        })

        mViewModel.getGroupResults().observe(this, Observer<Resource<List<GroupDevice>>> {
            if (it?.status == Status.SUCCESS) {
                groups = it.data
            }
            createShareCode()
        })

        mViewModel.getSettingResults().observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS && it.data?.size == 2) {
                settings = it.data
            }
            mViewModel.loadDevices(lampCategoryType)
        })

        loadShareData()
    }



    private fun getShareJson(): String {
        val objJson = JSONObject()
        val jsonLightStates = JSONArray()
        // settings
        if (settings != null) {
            objJson.put(DEVICE_TYPE, lampCategoryType+1)
            objJson.put(NETWORK_KEY, settings!![1].networkKey)
            val lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$lampCategoryType", -1)
            objJson.put(CURRENT_ID, lastUsedDeviceId)
            objJson.put(NEXT_DEVICE_INDEX_KEY, settings!![0].nextDeviceIndex )
            objJson.put(NEXT_GROUP_INDEX_KEY, settings!![1].nextGroupIndex )
        }

        // devices
        val jsonDevices = JSONArray()
        if (deviceModels != null) {
            for (deviceModel in deviceModels!!) {
                val deviceJson = JSONObject()
                deviceJson.put(DEVICE_ID_KEY, deviceModel.device?.id)
                deviceJson.put(DEVICE_NAME_KEY, deviceModel.device?.device?.name)
                deviceJson.put(DEVICE_HASH_KEY, deviceModel.device?.hash)
                val deviceStateJson = JSONObject()
                val lightStateJson = JSONObject()
                val lightState = deviceModel.device?.state
                getStateJson(lightStateJson, lightState)
                deviceStateJson.put("" + deviceModel.device?.id, lightStateJson)
                jsonDevices.put(deviceJson)
                jsonLightStates.put(deviceStateJson)

                val modelsSupported = deviceModel.device?.getModelsSupported()
                val models = deviceModel.modelList
                val jsonModelsList = JSONArray()
                if (modelsSupported != null && models != null) {
                    for (type in modelsSupported) {
                        val modelJSON = JSONObject()
                        modelJSON.put(MODEL_TYPE_KEY, type)
                        val jsonGroupsAssigned = JSONArray()
                        for (j in 0 until models.size) {
                            val groupJSON = JSONObject()
                            groupJSON.put(MODEL_GROUP_X_KEY, models[j].groupId)
                            jsonGroupsAssigned.put(groupJSON)
                        }
                        modelJSON.put(MODEL_GROUP_INSTANCES_KEY, jsonGroupsAssigned)
                        jsonModelsList.put(modelJSON)
                    }
                }
                // add models to the json device
                deviceJson.put(DEVICE_MODELS_KEY, jsonModelsList);
            }
        }
        objJson.put(DEVICES_KEY, jsonDevices)

        // groups
        val jsonGroups = JSONArray()
        if (groups != null) {
            for (group in groups!!) {
                val groupJson = JSONObject()
                groupJson.put(GROUP_ID_KEY, group.id)
                groupJson.put(GROUP_NAME_KEY, group.device?.name)
                val groupStateJson = JSONObject()
                val lightStateJson = JSONObject()
                val lightState = group.state
                getStateJson(lightStateJson, lightState)
                groupStateJson.put("" + group.id, lightStateJson)
                jsonGroups.put(groupJson)
                jsonLightStates.put(groupStateJson)
            }
        }
        objJson.put(GROUPS_KEY, jsonGroups)
        objJson.put("exts", jsonLightStates)

        return objJson.toString()

    }

    private fun getStateJson(lightStateJson: JSONObject, lightState: ControlState?) {
        if (lightState != null) {
            lightStateJson.put("isOn", lightState.on != 0)
            lightStateJson.put("type", lampCategoryType+1)
            lightStateJson.put("isOnOpenTimer", lightState.openTimerOn != 0)
            lightStateJson.put("isOnCloseTimer", lightState.closeTimerOn != 0)
            lightStateJson.put("colorTemperature", lightState.colorTemperature)
            if (lightState.colorPosition!! > 0 && lightState.colorPosition <= Math.PI) {
                lightStateJson.put("colorPosition", -lightState.colorPosition)
            } else {
                lightStateJson.put("colorPosition", Math.PI * 2 - lightState.colorPosition)
            }
            lightStateJson.put("sceneMode", Integer.parseInt("f" + lightState.sceneMode, 16))
            lightStateJson.put("isSelectedChangeMode", lightState.changeMode != -1)
            lightStateJson.put("changeMode", lightState.changeMode)
            lightStateJson.put("isLight", lightState.light != 0)
            lightStateJson.put("openTimer", lightState.openTimer)
            lightStateJson.put("brightness", lightState.brightness.plus(15))
            lightStateJson.put("closeTimer", lightState.closeTimer)
        }
    }

    private fun loadShareData() {
        if (lampCategoryType != -1) {
            generatingDialog.isCancelable = false
            generatingDialog.show(fragmentManager, "ShareCodeGeneraeFragment")
            mViewModel.loadSettings(lampCategoryType)
        }
    }

    private fun createShareCode() {
        val dataBaseJson = getShareJson()
        val configuration = URLEncoder.encode(dataBaseJson, "UTF-8").replace("+", "%20")
        if (!TextUtils.isEmpty(configuration)) {
            val key = "IHomey_" + System.currentTimeMillis() + ".json"
            val auth = Auth.create(ACCESS_KEY, SECRET_KEY)
            UploadManager(Configuration.Builder().build()).put(configuration.toByteArray(), key, auth.uploadToken(BUCKET_NAME), { key, info, response ->
                mViewDataBinding.tvQrCodeGenerateResult.visibility = View.VISIBLE
                if (info.isOK) {
                    AppExecutors().diskIO().execute({
                        bitmap = QRCodeEncoder.syncEncodeQRCode(DOMAIN + key, dip2px(215f))
                        runOnUiThread({
                            generatingDialog.dismiss()
                            mViewDataBinding.ivQrCode.setImageBitmap(bitmap)
                            mViewDataBinding.tvQrCodeGenerateResult.setText(R.string.qrCode_generate_done)
                        })
                    })
                } else {
                    runOnUiThread({
                        generatingDialog.dismiss()
                        mViewDataBinding.tvQrCodeGenerateResult.setText(R.string.qrCode_generate_fail)
                    })
                }
            }, null)
        }
    }

    inner class ShareHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> (view.context as Activity).finish()
                R.id.tv_qrCode_generate -> loadShareData()
                R.id.tv_qrCode_save -> {
                    requestWriteExternalStoragePermission()
                }
            }
        }
    }

    private fun requestWriteExternalStoragePermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        when (permissionStatus) {
            PackageManager.PERMISSION_GRANTED -> saveImageToGallery(bitmap, "ihomey/img")
            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
        } else {
            when (requestCode) {
                PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> saveImageToGallery(bitmap, "ihomey/img")
            }
        }
    }
}