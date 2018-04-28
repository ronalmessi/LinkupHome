package com.ihomey.linkuphome.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.ActivitySplashBinding
import java.util.*


/**
 * Created by dongcaizheng on 2017/12/19.
 */
class SplashActivity : BaseActivity() {

    private lateinit var alphaAnimation: AlphaAnimation
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setDefaultLanguage()
        super.onCreate(savedInstanceState)
        initAlphaAnimation()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        checkPermission()
    }

    private fun setDefaultLanguage() {
        val currentLanguage = Locale.getDefault().displayLanguage.toLowerCase()
        when (currentLanguage) {
            "español" -> setDefaultLanguage("es")
            "deutsch" -> setDefaultLanguage("de")
            "français" -> setDefaultLanguage("fr")
            "中文" -> setDefaultLanguage("zh")
            else -> setDefaultLanguage("en")
        }
    }

    private fun initAlphaAnimation() {
        alphaAnimation = AlphaAnimation(0f, 1.0f)
        alphaAnimation.duration = 1000
        alphaAnimation.fillAfter = true
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
                finish()
            }
        })
    }

    private fun checkPermission() {
        val permissionStatus = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.CAMERA)
        when (permissionStatus) {
            PackageManager.PERMISSION_GRANTED -> binding.textSplashSlogan.startAnimation(alphaAnimation)
            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA), 100)
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
            binding.textSplashSlogan.startAnimation(alphaAnimation)
        }
    }

}