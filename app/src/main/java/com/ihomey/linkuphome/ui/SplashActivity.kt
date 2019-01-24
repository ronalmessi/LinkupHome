package com.ihomey.linkuphome.ui

import android.content.Intent
import android.os.Bundle
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.HomeActivity
import com.ihomey.linkuphome.R



class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.postDelayed({
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
            finish()
        }, 400)
    }

//    private fun checkPermission() {
//        val permissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.CAMERA)
//        when (permissionStatus) {
//            PackageManager.PERMISSION_GRANTED -> binding.ivSplashLogo.startAnimation(alphaAnimation)
//            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA), 100)
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                intent.data = Uri.fromParts("package", packageName, null)
//                startActivityForResult(intent, 101)
//            }
//        } else {
//            binding.ivSplashLogo.startAnimation(alphaAnimation)
//        }
//    }
}

