package com.ihomey.linkuphome.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.inform.InformActivity

class SplashActivity : BaseActivity() {

    private lateinit var viewModel: SplashActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SplashActivityViewModel::class.java)
        viewModel.getSetting().observe(this, Observer<Resource<Setting>> {
            if (it?.status == Status.SUCCESS) {
                var hasZone by PreferenceHelper("hasZone", false)
                hasZone = it.data != null
                checkPermission()
            }
        })
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
        startActivity(Intent(this@SplashActivity, if (hasAgreed) HomeActivity::class.java else InformActivity::class.java))
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
        finish()
    }
}

