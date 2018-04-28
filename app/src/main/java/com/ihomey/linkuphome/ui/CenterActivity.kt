package com.ihomey.linkuphome.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.ActivityCenterBinding
import com.ihomey.linkuphome.listener.EventHandler


/**
 * Created by dongcaizheng on 2017/12/21.
 */
class CenterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCenterBinding>(this, R.layout.activity_center)
        binding.handlers = EventHandler()
    }

}