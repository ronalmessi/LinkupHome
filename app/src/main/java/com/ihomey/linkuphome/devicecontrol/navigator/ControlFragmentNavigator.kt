package com.ihomey.linkuphome.devicecontrol.navigator

import androidx.navigation.NavController
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device

class ControlFragmentNavigator: SceneControlNavigation, SwitchTimerControlNavigation {

    private var navController: NavController? = null

    override fun openSceneControlPage() {

    }

    override fun openSwitchTimerControlPage(isRepeatable: Boolean) {

    }

    fun openDeviceControlPage() {
        navController?.navigate(R.id.action_tab_devices_to_deviceControlFragment)
    }

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}