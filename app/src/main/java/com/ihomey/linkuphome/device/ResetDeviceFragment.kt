package com.ihomey.linkuphome.device

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.LocaleHelper
import kotlinx.android.synthetic.main.reset_device_fragment.*

class ResetDeviceFragment : BaseFragment() {

    companion object {
        fun newInstance() = ResetDeviceFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.reset_device_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        var currentLanguage = LocaleHelper.getLanguage(context)
        when {
            TextUtils.equals("zh-rCN", currentLanguage) -> currentLanguage = "zh-Hans"
            TextUtils.equals("zh-rTW", currentLanguage) -> currentLanguage = "zh-Hant"
            TextUtils.equals("pt", currentLanguage) -> currentLanguage = "pt-PT"
        }
        context?.let { Glide.with(it).load(AppConfig.RESET_DEVICE_BASE_URL + currentLanguage + ".jpg").into(iv_device_reset_guide2) }
    }
}
