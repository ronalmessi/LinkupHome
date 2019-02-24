package com.ihomey.linkuphome.setting

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.entity.ZoneSetting
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.BottomNavigationVisibilityListener
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import kotlinx.android.synthetic.main.setting_fragment.*

class SettingFragment : BaseFragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }


    private lateinit var listener: BottomNavigationVisibilityListener
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BottomNavigationVisibilityListener
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
        listener.showBottomNavigationBar(true)
        infoTextLayout_setting_current_zone.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_zoneSettingFragment)
        }
        infoTextLayout_setting_more.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_moreFragment)
        }
        infoTextLayout_setting_instructions.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_instructionsFragment)
        }
        infoTextLayout_setting_language.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_tab_setting_to_languageSettingFragment)
        }
    }

}
