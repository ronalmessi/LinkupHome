package com.ihomey.linkuphome.inform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.Zone
import com.ihomey.linkuphome.setTranslucentStatus
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.zone.CreateZoneViewModel

class InformActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucentStatus()
        setContentView(R.layout.activity_home)
        scheduleScreen()
    }

    private fun scheduleScreen() {
        val finalHost = NavHostFragment.create(R.navigation.nav_inform)
        supportFragmentManager.beginTransaction().replace(R.id.nav_host, finalHost).setPrimaryNavigationFragment(finalHost).commit()
    }
}
