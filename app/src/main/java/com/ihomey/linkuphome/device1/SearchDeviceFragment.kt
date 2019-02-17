package com.ihomey.linkuphome.device1

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.BridgeListener
import kotlinx.android.synthetic.main.search_device_fragment.*

class SearchDeviceFragment : BaseFragment() {

    companion object {
        fun newInstance() = SearchDeviceFragment()
    }

    private lateinit var listener: BridgeListener

    private val icons = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)
    private lateinit var viewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_device_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BridgeListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.getBridgeState().observe(this, Observer<Boolean> {
            if (it != null && it) {
                val bundle = Bundle()
                bundle.putInt("deviceType", arguments?.getInt("deviceType")!!)
                Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_searchDeviceFragment_to_connectDeviceFragment,bundle)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack() }
        btn_device_reset.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_searchDeviceHintFragment_to_resetDeviceFragment) }
        iv_device_connect_lamp_icon.setImageResource(icons[arguments?.getInt("deviceType", 1)!!])
    }

    override fun onResume() {
        super.onResume()
//        listener.connectBridge()
    }

}
