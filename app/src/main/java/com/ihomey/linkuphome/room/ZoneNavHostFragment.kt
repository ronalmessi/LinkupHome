package com.ihomey.linkuphome.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseNavHostFragment


class ZoneNavHostFragment : BaseNavHostFragment() {


    fun newInstance(): ZoneNavHostFragment {
        return ZoneNavHostFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.navhost_zone_fragment, container, false)
    }

}
