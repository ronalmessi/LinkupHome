package com.ihomey.linkuphome.device

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceConnectStepPageAdapter
import com.ihomey.linkuphome.databinding.FragmentDeviceConnectBinding
import com.ihomey.linkuphome.listener.IDeviceConnectStepListener
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.jackandphantom.blurimage.BlurImage


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceConnectFragment : BaseFragment(), IDeviceConnectStepListener {

    private lateinit var mViewDataBinding: FragmentDeviceConnectBinding

    fun newInstance(categoryType: Int, isReConnect: Boolean): DeviceConnectFragment {
        val addProductFragment = DeviceConnectFragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        bundle.putBoolean("isReConnect", isReConnect)
        addProductFragment.arguments = bundle
        return addProductFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_connect, container, false)
        mViewDataBinding.handlers = EventHandler()
        val bitmap = BlurImage.with(context).load(R.mipmap.lamp_category_bg).intensity(20f).imageBlur
        mViewDataBinding.clDeviceConnect.background = BitmapDrawable(resources, bitmap)
        val isReConnect = arguments.getBoolean("isReConnect", false)
        mViewDataBinding.viewpagerDeviceConnect.adapter = DeviceConnectStepPageAdapter(arguments.getInt("categoryType", -1), isReConnect, childFragmentManager)
        if (isReConnect) mViewDataBinding.viewpagerDeviceConnect.currentItem = 2
        return mViewDataBinding.root
    }

    override fun setStep(step: Int) {
        mViewDataBinding.viewpagerDeviceConnect.setCurrentItem(step, true)
    }

    override fun goToDeviceResetFragment() {
        (activity as IFragmentStackHolder).replaceFragment(R.id.container, DeviceResetFragment().newInstance())
    }

    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> (view.context as Activity).onBackPressed()
            }
        }
    }
}