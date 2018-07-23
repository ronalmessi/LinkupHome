package com.ihomey.linkuphome.device

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SingleDevice
import com.ihomey.linkuphome.databinding.FragmentDeviceConnectStep2Binding
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.main.LampFragment
import com.ihomey.linkuphome.viewmodel.MainViewModel


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceConnectStep2Fragment : BaseFragment() {

    private var listener: BridgeListener? = null
    private var mViewModel: MainViewModel? = null
    private lateinit var mViewDataBinding: FragmentDeviceConnectStep2Binding
    private val icons = arrayListOf(R.mipmap.lamp_icon_lawn_unadded, R.mipmap.lamp_icon_rgb_unadded, R.mipmap.lamp_icon_warm_cold_unadded, R.mipmap.lamp_icon_led_unadded, R.mipmap.lamp_icon_outdoor_unadded)

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
        listener?.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_connect_step2, container, false)
        mViewDataBinding.handlers = EventHandler()
        mViewDataBinding.ivDeviceConnectLampIcon.setImageResource(icons[arguments.getInt("categoryType", 0)])
        if (arguments.getBoolean("isReConnect", false)) {
            mViewDataBinding.tvDeviceConnectStep2Hint.text = "设备已断开，正在尝试自动连接"
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
        mViewModel?.isBridgeStateChanged()?.observe(this, Observer<Void> {
            Log.d("aa", "111")
            mViewDataBinding.ivDeviceConnectLampIcon.postDelayed({
                showLamp()
            }, 8000)
        })


        mViewModel?.hasScannedDevice()?.observe(this, Observer<Void> {
            Log.d("aa", "2222")
            showLamp()
        })


        if (listener != null) {
            if (listener?.isBridgeConnected()!!) {
                Log.d("aa", "33333")
                mViewDataBinding.ivDeviceConnectLampIcon.postDelayed({
                    showLamp()
                }, 8000)
            } else {
                listener?.connectBridge()
            }
        }
    }

    private fun showLamp() {
//        val transition = AutoTransition()
//        transition.duration = 2850
//        transition.interpolator = AccelerateDecelerateInterpolator()
//        TransitionManager.beginDelayedTransition(mViewDataBinding.clDeviceConnectStep2, transition)
//        constraintSet.applyTo(mViewDataBinding.clDeviceConnectStep2)
//        mViewDataBinding.clDeviceConnectStep2.postDelayed({
        if (activity != null) {
            activity.onBackPressed()
            if (!arguments.getBoolean("isReConnect", false)) {
                Log.d("aa", "4444444")
                (activity as IFragmentStackHolder).replaceFragment(R.id.container, LampFragment().newInstance(arguments.getInt("categoryType", 0)))
            }
        }
//        }, 2850)
    }

    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.btn_device_connect_reset -> (activity as IFragmentStackHolder).replaceFragment(R.id.container, DeviceResetFragment().newInstance(arguments.getInt("categoryType", 0)))
            }
        }
    }
}