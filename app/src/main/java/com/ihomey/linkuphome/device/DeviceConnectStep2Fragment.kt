package com.ihomey.linkuphome.device

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clj.fastble.BleManager
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentDeviceConnectStep2Binding
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.main.MeshLampFragment
import com.ihomey.linkuphome.viewmodel.MainViewModel


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceConnectStep2Fragment : BaseFragment() {

    private lateinit var listener: BridgeListener
    private var mViewModel: MainViewModel? = null
    private lateinit var mViewDataBinding: FragmentDeviceConnectStep2Binding
    private val icons = arrayListOf(R.mipmap.lamp_icon_lawn_unadded, R.mipmap.lamp_icon_rgb_unadded, R.mipmap.lamp_icon_warm_cold_unadded, R.mipmap.lamp_icon_led_unadded, R.mipmap.lamp_icon_outdoor_unadded, R.mipmap.lamp_icon_bed_unadded)

    fun newInstance(categoryType: Int, isReConnect: Boolean): DeviceConnectStep2Fragment {
        val addProductFragment = DeviceConnectStep2Fragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        bundle.putBoolean("isReConnect", isReConnect)
        addProductFragment.arguments = bundle
        return addProductFragment
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BridgeListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_connect_step2, container, false)
        mViewDataBinding.handlers = EventHandler()
        mViewDataBinding.ivDeviceConnectLampIcon.setImageResource(icons[arguments.getInt("categoryType", 0)])
        if (arguments.getBoolean("isReConnect", false)) {
            mViewDataBinding.tvDeviceConnectStep2Hint.text = getString(R.string.bridge_disconnected)
            mViewDataBinding.btnDeviceConnectReset.visibility = View.GONE
        } else {
            mViewDataBinding.tvDeviceConnectStep2Hint.text = getString(R.string.device_connect_step2_hint)
            mViewDataBinding.btnDeviceConnectReset.visibility = View.VISIBLE
        }
        return mViewDataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel?.getBridgeState()?.observe(this, Observer<Boolean> {
            if (it != null && it) {
                showLamp()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (arguments.getInt("categoryType", 0) < 5) {
            if (!arguments.getBoolean("isReConnect", false)) {
                listener.connectBridge()
            }
        } else {
            if (BleManager.getInstance().isBlueEnable) {
                mViewDataBinding.tvDeviceConnectStep2Hint.postDelayed({
                    if (activity != null) {
                        activity.onBackPressed()
                        (activity as IFragmentStackHolder).replaceFragment(R.id.container, BleLampFragment().newInstance(arguments.getInt("categoryType", 0)))
                    }
                }, 1000)
            } else {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 0x01)
            }
        }
    }

    private fun showLamp() {
        mViewDataBinding.tvDeviceConnectStep2Hint.postDelayed({
            if (activity != null) {
                activity.onBackPressed()
                (activity as IFragmentStackHolder).replaceFragment(R.id.container, MeshLampFragment().newInstance(arguments.getInt("categoryType", 0)))
            }
        }, 1000)
    }


    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.btn_device_connect_reset -> (activity as IFragmentStackHolder).replaceFragment(R.id.container, DeviceResetFragment().newInstance(arguments.getInt("categoryType", 0)))
            }
        }
    }
}