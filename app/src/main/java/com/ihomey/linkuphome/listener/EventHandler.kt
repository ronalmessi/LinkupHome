package com.ihomey.linkuphome.listener

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.ui.CenterActivity
import com.ihomey.linkuphome.ui.WebViewActivity

/**
 * Created by dongcaizheng on 2018/4/8.
 */
open class EventHandler {

    fun onClick(view: View) {
        Log.d("aa","onSpeedChange----"+"---")
        when (view.id) {
            R.id.toolbar_back -> (view.context as Activity).finish()
            R.id.center_layout_instructions -> view.context.startActivity(Intent(view.context, WebViewActivity::class.java))
            R.id.welcome_iv_center -> view.context.startActivity(Intent(view.context, CenterActivity::class.java))
            R.id.welcome_btn_open -> view.context.startActivity(Intent(view.context, CenterActivity::class.java))
        }
    }
}