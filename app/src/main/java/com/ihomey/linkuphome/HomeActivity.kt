package com.ihomey.linkuphome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.iclass.soocsecretary.util.PreferenceHelper

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucentStatus()
        setContentView(R.layout.activity_home)
        scheduleScreen()
    }

    private fun scheduleScreen() {
        val hasZone by PreferenceHelper("hasZone", false)
        val finalHost = NavHostFragment.create(if(hasZone) R.navigation.nav_zone_init else R.navigation.nav_zone_init)
        supportFragmentManager.beginTransaction().replace(R.id.nav_host, finalHost).setPrimaryNavigationFragment(finalHost).commit()
    }
}
