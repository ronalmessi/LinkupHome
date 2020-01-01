package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseNavHostFragment


class SettingNavHostFragment : BaseNavHostFragment() {


    fun newInstance(): SettingNavHostFragment {
        return SettingNavHostFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.navhost_setting_fragment, container, false)
    }

}
