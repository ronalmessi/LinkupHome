package com.ihomey.linkuphome.splash

import android.Manifest
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
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.inform.InformActivity
import com.ihomey.linkuphome.toast

class SplashActivity : BaseActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel= ViewModelProviders.of(this).get(SplashViewModel::class.java)
        checkPermission()
    }

    private fun checkPermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        when (permissionStatus) {
            PackageManager.PERMISSION_GRANTED -> scheduleScreen()
            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE), 100)
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
        if(hasAgreed){
         getIMEI().let { it1 -> splashViewModel.getRemoteCurrentZone(it1).observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
//                homeActivityViewModel.setCurrentZoneId(it.data?.id)
//                Navigation.findNavController(it0).navigate(R.id.action_createZoneFragment_to_homeFragment)
                goToHomeActivity()
            }else  if (it?.status == Status.ERROR) {
                it.message?.let { it2 -> toast(it2) }
                goToHomeActivity()
            }
        }) }
        }else{
            goToInformActivity()
        }
    }

    private fun goToHomeActivity() {
        splashViewModel.getLocalCurrentZone().observe(this, Observer<Resource<Int>> {
            if (it?.status == Status.SUCCESS) {
                Log.d("aa","---"+it.data)
                val intent=Intent(this@SplashActivity, HomeActivity::class.java)
                intent.putExtra("currentZoneId",it.data)
                startActivity(intent)
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
                finish()
            }
        })
    }

    private fun goToInformActivity() {
        startActivity(Intent(this@SplashActivity, InformActivity::class.java))
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
        finish()
    }
}

