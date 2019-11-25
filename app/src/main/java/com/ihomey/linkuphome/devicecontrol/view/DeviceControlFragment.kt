package com.ihomey.linkuphome.devicecontrol.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.device1.DeviceNavHostFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import kotlinx.android.synthetic.main.device_control_fragment.*


class DeviceControlFragment : BaseFragment() {

    private lateinit var mViewModel: HomeActivityViewModel

    private  var controlView:BaseControlView?=null

    private lateinit var device:Device

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hideBottomBar()
        return inflater.inflate(R.layout.device_control_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(view).popBackStack() }
    }

    private fun hideBottomBar() {
        parentFragment?.parentFragment?.let { if (it is DeviceNavHostFragment) it.showBottomNavigationBar(false) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(HomeActivityViewModel::class.java)
            mViewModel.getCurrentControlDevice().observe(viewLifecycleOwner, Observer<Device> { it0 ->
                device=it0
                tv_title.text = it0.name
                context?.let {it1->
                    ControlViewFactory().createControlView(it0.type,it1,this)?.let { controlView=it
                        rootView.addView(it.getControlView(),ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                    }
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        for (f in childFragmentManager.fragments) {
            if (f is RgbControlView || f is WarmColdControlView) {
                childFragmentManager.beginTransaction().remove(f).commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        controlView?.bindTo(device)
    }
}