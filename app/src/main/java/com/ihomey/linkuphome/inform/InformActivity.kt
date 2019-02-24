package com.ihomey.linkuphome.inform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.setTranslucentStatus

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
