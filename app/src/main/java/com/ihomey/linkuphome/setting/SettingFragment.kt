package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel

import kotlinx.android.synthetic.main.setting_fragment.*

class SettingFragment : BaseFragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS) {
                infoTextLayout_setting_current_zone.setTextValue(it.data?.name?:"")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as SettingNavHostFragment).showBottomNavigationBar(true) }

        infoTextLayout_setting_current_zone.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_zoneSettingFragment)
        }
        infoTextLayout_setting_more.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_moreFragment)
        }
        infoTextLayout_setting_faqs.setOnClickListener {
            var currentLanguage = LocaleHelper.getLanguage(context)
            when {
                TextUtils.equals("zh-rCN",currentLanguage) -> currentLanguage="zh"
                TextUtils.equals("zh-rTW",currentLanguage) -> currentLanguage="zh-Hant"
                TextUtils.equals("pt",currentLanguage) -> currentLanguage="pt-PT"
            }
            val bundle=Bundle()
            bundle.putString("sourceUrl",AppConfig.FAQ_BASE_URL+currentLanguage)
            bundle.putString("title",getString(R.string.title_faqs))
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_webViewFragment,bundle)
        }
        infoTextLayout_setting_instructions.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_instructionFragment)
        }
        infoTextLayout_setting_language.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_languageSettingFragment)
        }
    }

}
