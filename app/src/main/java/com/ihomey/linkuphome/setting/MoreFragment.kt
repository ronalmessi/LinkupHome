package com.ihomey.linkuphome.setting

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import kotlinx.android.synthetic.main.more_fragment.*

class MoreFragment : BaseFragment() {

    companion object {
        fun newInstance() = MoreFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.more_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoTextLayout_setting_name.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_moreFragment2_to_termsOfUseFragment) }
        infoTextLayout_setting_avatar.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_moreFragment2_to_privacyAgreementFragment) }
    }
}
