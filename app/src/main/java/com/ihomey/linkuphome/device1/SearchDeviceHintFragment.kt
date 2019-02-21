package com.ihomey.linkuphome.device1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.base.BaseFragment

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
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        btn_device_reset.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_searchDeviceHintFragment_to_resetDeviceFragment2)
        }
        btn_next.setOnClickListener {
            val bundle = Bundle()
            arguments?.getInt("deviceType")?.let { it1 -> bundle.putInt("deviceType", it1) }
            Navigation.findNavController(it).navigate(R.id.action_searchDeviceHintFragment_to_searchDeviceFragment2, bundle)
        }
    }
}
