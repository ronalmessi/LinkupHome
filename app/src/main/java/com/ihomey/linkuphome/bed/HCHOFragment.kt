package com.ihomey.linkuphome.bed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.controller.BedController
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.FragmentEnvironmentHchoBinding
import com.ihomey.linkuphome.listener.SensorValueListener
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.viewmodel.MainViewModel
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.view.animation.LinearInterpolator


/**
 * Created by dongcaizheng on 2017/12/27.
 */
class HCHOFragment : BaseFragment(), SensorValueListener {

    private lateinit var mViewDataBinding: FragmentEnvironmentHchoBinding

    private var mViewModel: MainViewModel? = null

    private var deviceMacAddress: String? = null

    private val controller: BedController = BedController()

    fun newInstance(): HCHOFragment {
        return HCHOFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> { it ->
            if (it?.status == Status.SUCCESS) {
                deviceMacAddress = it.data?.device?.macAddress
                deviceMacAddress?.let {
                    controller.getHCHOValue(it)
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_environment_hcho, container, false)
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val environmentMonitorFragment = parentFragment as EnvironmentMonitorFragment
        environmentMonitorFragment.setSensorValueListener(this)
        val bleLampFragment = parentFragment.parentFragment as BleLampFragment
        mViewDataBinding.toolbarOpenDrawer.setOnClickListener {
            bleLampFragment.openDrawer()
        }
        mViewDataBinding.flRefresh.setOnClickListener {
            deviceMacAddress?.let { controller.getHCHOValue(it) }
            startAnimation()
        }
        startAnimation()
    }

    override fun onSensorValueChanged(sensorValue: String) {
        if (sensorValue.startsWith("fe01d101da0004c102")) {
            val hchoValue = Integer.parseInt(sensorValue.substring(18, 20), 16) * 256 + Integer.parseInt(sensorValue.substring(20, 22), 16)
            mViewDataBinding.hchoView.setValue(hchoValue)
            stopAnimation()
        }
    }

    private fun startAnimation() {
        mViewDataBinding.btnRefresh.isActivated = true
        mViewDataBinding.btnHome.isActivated = true
        val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
        rotate.interpolator = LinearInterpolator()
        mViewDataBinding.btnRefresh.startAnimation(rotate)
    }

    private fun stopAnimation() {
        mViewDataBinding.btnRefresh.isActivated = false
        mViewDataBinding.btnHome.isActivated = false
        mViewDataBinding.btnRefresh.clearAnimation()
    }
}