package com.ihomey.linkuphome.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.BottomNavigationVisibilityListener
import kotlinx.android.synthetic.main.more_fragment.*

class MoreFragment : BaseFragment() {

    companion object {
        fun newInstance() = MoreFragment()
    }

    private lateinit var listener: BottomNavigationVisibilityListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BottomNavigationVisibilityListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.more_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener.showBottomNavigationBar(false)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        infoTextLayout_setting_name.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_moreFragment_to_termsOfUseFragment) }
        infoTextLayout_setting_avatar.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_moreFragment_to_privacyAgreementFragment) }
    }
}
