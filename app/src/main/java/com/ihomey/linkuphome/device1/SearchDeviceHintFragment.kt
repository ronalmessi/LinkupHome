package com.ihomey.linkuphome.device1

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import kotlinx.android.synthetic.main.search_device_hint_fragment.*

class SearchDeviceHintFragment : BaseFragment() {

    companion object {
        fun newInstance() = SearchDeviceHintFragment()
    }

    private lateinit var viewModel: SearchDeviceHintViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_device_hint_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchDeviceHintViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack()}
        btn_device_reset.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_searchDeviceHintFragment_to_resetDeviceFragment) }
        btn_next.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_searchDeviceHintFragment_to_searchDeviceFragment) }
    }
}
