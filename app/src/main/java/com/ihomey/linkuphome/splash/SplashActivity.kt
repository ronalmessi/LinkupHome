package com.ihomey.linkuphome.splash

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.ihomey.linkuphome.dialog.PermissionPromptDialogFragment
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.inform.InformActivity
import com.ihomey.linkuphome.toast

class SplashActivity : BaseActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        checkPermission()
    }

    private fun checkPermission() {
        val accessCoarseLocationPermissionStatus=ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        val accessFineLocationPermissionStatus=ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        val readPhonePermissionStatus=ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.READ_PHONE_STATE)
        if(accessCoarseLocationPermissionStatus==PackageManager.PERMISSION_GRANTED&&accessFineLocationPermissionStatus==PackageManager.PERMISSION_GRANTED&&readPhonePermissionStatus== PackageManager.PERMISSION_GRANTED){
            synchronizeData()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE), 100)
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
        if(requestCode==101) checkPermission()
    }

    private fun showPermissionPromptDialog(){
        val dialog = PermissionPromptDialogFragment().newInstance(getString(R.string.msg_notes),getString(R.string.hint_request_location_permission),getString(R.string.action_confirm))
        dialog.setConfirmButtonClickListener(object : PermissionPromptDialogFragment.ConfirmButtonClickListener {
            override fun onConfirm() {
                checkPermission()
            }
        })
        dialog.show(supportFragmentManager, "PermissionPromptDialogFragment")
    }

    private fun synchronizeData() {
        getIMEI().let { it1 ->
            splashViewModel.getRemoteCurrentZone(it1).observe(this, Observer<Resource<ZoneDetail>> {
                if (it?.status == Status.SUCCESS) {
                    scheduleScreen()
                } else if (it?.status == Status.ERROR) {
                    scheduleScreen()
                }
            })
        }
    }

    private fun scheduleScreen() {
        val hasAgreed by PreferenceHelper("hasAgreed", false)
        splashViewModel.getCurrentZoneId().observe(this, Observer<Resource<Int>> {
            if (it?.status == Status.SUCCESS) {
                val intent = Intent(this@SplashActivity, if (hasAgreed) HomeActivity::class.java else  InformActivity::class.java)
                intent.putExtra("currentZoneId", it.data)
                startActivity(intent)
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
                finish()
            }
        })
    }
}

