package com.ihomey.linkuphome.inform

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseActivity

class InformActivity : BaseActivity() {

    private lateinit var mViewModel: InformViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(InformViewModel::class.java)
        setContentView(R.layout.activity_home)
        scheduleScreen()
    }

    private fun scheduleScreen() {
        mViewModel.setCurrentZoneId(intent.extras?.getInt("currentZoneId"))
        val finalHost = NavHostFragment.create(R.navigation.nav_inform)
        supportFragmentManager.beginTransaction().replace(R.id.nav_host, finalHost).setPrimaryNavigationFragment(finalHost).commit()
    }
}
