package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.home.HomeFragment
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
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(false)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        infoTextLayout_setting_name.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_moreFragment_to_termsOfUseFragment) }
        infoTextLayout_setting_avatar.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_moreFragment_to_privacyAgreementFragment) }
    }
}
