package com.ihomey.linkuphome.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.dialog.InformDialogFragment
import com.ihomey.linkuphome.dialog.PermissionPromptDialogFragment
import com.ihomey.linkuphome.getDeviceId
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.inform.InformActivity
import com.ihomey.linkuphome.listener.InformDialogInterface

class SplashActivity : BaseActivity(), InformDialogInterface {


    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        val hasAgreed by PreferenceHelper("hasAgreed", false)
        if (hasAgreed) {
            checkPermission()
        } else {
            showInformDialog()
        }
    }

    private fun checkPermission() {
        val accessCoarseLocationPermissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        val accessFineLocationPermissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        val accessWriteExternalStoragePermissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val accessReadExternalStoragePermissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        val readPhonePermissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.READ_PHONE_STATE)
        if (accessCoarseLocationPermissionStatus == PackageManager.PERMISSION_GRANTED && accessFineLocationPermissionStatus == PackageManager.PERMISSION_GRANTED && readPhonePermissionStatus == PackageManager.PERMISSION_GRANTED && accessWriteExternalStoragePermissionStatus == PackageManager.PERMISSION_GRANTED && accessReadExternalStoragePermissionStatus == PackageManager.PERMISSION_GRANTED) {
            synchronizeData()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showPermissionPromptDialog()
        } else {
            synchronizeData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) checkPermission()
    }

    override fun onAgree() {
        var hasAgreed by PreferenceHelper("hasAgreed", false)
        hasAgreed=true
        checkPermission()
    }

    override fun onDisAgree() {
        finish()
    }

    private fun showPermissionPromptDialog() {
        val dialog = PermissionPromptDialogFragment().newInstance(getString(R.string.msg_notes), getString(R.string.hint_request_location_permission), getString(R.string.action_confirm))
        dialog.setConfirmButtonClickListener(object : PermissionPromptDialogFragment.ConfirmButtonClickListener {
            override fun onConfirm() {
                checkPermission()
            }
        })
        dialog.show(supportFragmentManager, "PermissionPromptDialogFragment")
    }

    private fun showInformDialog() {
        val dialog = InformDialogFragment()
        dialog.setInformDialogInterface(this)
        dialog.show(supportFragmentManager, "InformDialogFragment")
    }

    private fun synchronizeData() {
        getDeviceId().let { it1 ->
            splashViewModel.getRemoteCurrentZone(it1).observe(this, Observer<Resource<ZoneDetail>> {
                if (it?.status != Status.LOADING) scheduleScreen()
            })
        }
    }

    private fun scheduleScreen() {
        splashViewModel.getCurrentZoneId().observe(this, Observer<Resource<Int>> {
            if (it?.status == Status.SUCCESS) {
                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                intent.putExtra("currentZoneId", it.data ?: 0)
                startActivity(intent)
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
                finish()
            }
        })
    }
}

