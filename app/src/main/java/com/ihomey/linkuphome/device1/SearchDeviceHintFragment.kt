package com.ihomey.linkuphome.device1

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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_device_hint_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack() }
        btn_device_reset.setOnClickListener {
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_searchDeviceHintFragment_to_resetDeviceFragment)
        }
        btn_next.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("deviceType", arguments?.getInt("deviceType")!!)
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_searchDeviceHintFragment_to_searchDeviceFragment, bundle)
        }
    }
}
