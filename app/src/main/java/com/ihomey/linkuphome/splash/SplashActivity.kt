package com.ihomey.linkuphome.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.inform.InformActivity
import com.ihomey.linkuphome.zone.CreateZoneActivity

class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    private fun checkPermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        when (permissionStatus) {
            PackageManager.PERMISSION_GRANTED -> scheduleScreen()
            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", packageName, null)
                startActivityForResult(intent, 101)
            }
        } else {
            scheduleScreen()
        }
    }


    private fun scheduleScreen() {
        val hasAgreed by PreferenceHelper("hasAgreed", false)
        val currentZoneId by PreferenceHelper("currentZoneId", -1)
        startActivity(Intent(this@SplashActivity, if (!hasAgreed) InformActivity::class.java else {
            if (currentZoneId != -1) HomeActivity::class.java else CreateZoneActivity::class.java
        }))
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
        finish()
    }
}

