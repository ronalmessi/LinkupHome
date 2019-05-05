package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.LocaleHelper
import kotlinx.android.synthetic.main.more_fragment.*

class MoreFragment : BaseFragment() {

    companion object {
        fun newInstance() = MoreFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.more_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as SettingNavHostFragment).showBottomNavigationBar(false) }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        infoTextLayout_setting_name.setOnClickListener {
            val bundle=Bundle()
            bundle.putString("sourceUrl", AppConfig.USER_AGGREEMENT_URL)
            bundle.putString("title",getString(R.string.title_user_agreement))
            Navigation.findNavController(it).navigate(R.id.action_moreFragment_to_webViewFragment,bundle)
        }
        infoTextLayout_setting_avatar.setOnClickListener {
            val bundle=Bundle()
            bundle.putString("sourceUrl",AppConfig.PRIVACY_STATEMENt_URL)
            bundle.putString("title",getString(R.string.title_private_statement))
            Navigation.findNavController(it).navigate(R.id.action_moreFragment_to_webViewFragment,bundle)
        }
    }
}
