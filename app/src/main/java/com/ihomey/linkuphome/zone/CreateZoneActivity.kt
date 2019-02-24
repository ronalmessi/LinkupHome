package com.ihomey.linkuphome.zone


import android.os.Bundle
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.setTranslucentStatus


class CreateZoneActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        setTranslucentStatus()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) supportFragmentManager.beginTransaction().replace(R.id.container, CreateZoneFragment.newInstance(true)).commitNow()
    }
}
