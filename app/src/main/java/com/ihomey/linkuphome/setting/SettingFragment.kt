package com.ihomey.linkuphome.setting

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.Zone
import kotlinx.android.synthetic.main.setting_fragment.*

class SettingFragment : BaseFragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
        viewModel.getCurrentZone().observe(this, Observer<Resource<Zone>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                infoTextLayout_setting_current_zone.setTextValue(it.data.name)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoTextLayout_setting_current_zone.setOnClickListener {
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_zoneSettingFragment)
        }
        infoTextLayout_setting_more.setOnClickListener {
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_moreFragment2)
        }
        infoTextLayout_setting_instructions.setOnClickListener {
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_homeFragment_to_instructionsFragment)
        }
    }
}
