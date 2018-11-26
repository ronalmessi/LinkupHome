package com.ihomey.linkuphome.bed

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentEnvironmentMonitorBinding
import com.ihomey.linkuphome.listener.OnDrawerMenuItemClickListener
import com.ihomey.linkuphome.listener.SensorValueListener
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.toast

class EnvironmentMonitorFragment : BaseFragment(), OnDrawerMenuItemClickListener {

    private lateinit var mViewDataBinding: FragmentEnvironmentMonitorBinding

    private var listener: SensorValueListener? = null

    fun newInstance(): EnvironmentMonitorFragment {
        return EnvironmentMonitorFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_environment_monitor, container, false)
        val bleLampFragment = parentFragment as BleLampFragment
        bleLampFragment.setOnDrawerMenuItemClickListener(this)
        return mViewDataBinding.root
    }

    private fun showFragment(fragment: BaseFragment?) {
        if (fragment != null && !fragment.isAdded) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.inner_frag_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            registerSensorValueReceiver()
            showFragment(TemperatureFragment().newInstance())
        } else {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(sensorValueReceiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(sensorValueReceiver)
    }

    override fun onMenuItemClick(viewId: Int, position: Int) {
        when (position) {
            0 -> showFragment(TemperatureFragment().newInstance())
            1 -> showFragment(HumidityFragment().newInstance())
            2 -> showFragment(PM25Fragment().newInstance())
            3 -> showFragment(HCHOFragment().newInstance())
            4 -> showFragment(VOCFragment().newInstance())
        }
    }

    private fun registerSensorValueReceiver() {
        val lbm = LocalBroadcastManager.getInstance(context)
        val filter = IntentFilter()
        filter.addAction("com.ihomey.linkuphome.SENSOR_VALUE_CHANGED")
        lbm.registerReceiver(sensorValueReceiver, filter)
    }

    private val sensorValueReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sensorValue = intent.getStringExtra("sensorValue")
            if( sensorValue.startsWith("fe01d101da0004c2050101")) activity.toast( "正在播放音乐")
            if (listener != null) listener?.onSensorValueChanged(sensorValue)
        }
    }

    fun setSensorValueListener(sensorValueListener: SensorValueListener) {
        this.listener = sensorValueListener
    }
}