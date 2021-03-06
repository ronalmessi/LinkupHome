package com.ihomey.library.base

import android.os.Bundle
import android.util.Log
import com.akexorcist.localizationactivity.LocalizationActivity
import com.umeng.analytics.MobclickAgent

/**
 * Created by Administrator on 2017/6/16.
 */
abstract class BaseActivity : LocalizationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity", javaClass.simpleName + "-----onCreate")
    }


    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
        MobclickAgent.onPageEnd(this.javaClass.simpleName)
        Log.d("BaseActivity", javaClass.simpleName + "-----onPause")
    }

    override fun onStart() {
        super.onStart()
        Log.d("BaseActivity", javaClass.simpleName + "-----onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("BaseActivity", javaClass.simpleName + "-----onResume")
        MobclickAgent.onResume(this)
        MobclickAgent.onPageStart(this.javaClass.simpleName)
    }


    override fun onStop() {
        super.onStop()
        Log.d("BaseActivity", javaClass.simpleName + "-----onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BaseActivity", javaClass.simpleName + "-----onDestroy")
    }
}