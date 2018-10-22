package com.ihomey.linkuphome.device

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentDeviceConnectBinding
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.jackandphantom.blurimage.BlurImage


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceConnectFragment : BaseFragment(), IFragmentStackHolder {

    private lateinit var mViewDataBinding: FragmentDeviceConnectBinding

    fun newInstance(categoryType: Int, hasConnected: Boolean, isReConnect: Boolean): DeviceConnectFragment {
        val deviceConnectFragment = DeviceConnectFragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        bundle.putBoolean("hasConnected", hasConnected)
        bundle.putBoolean("isReConnect", isReConnect)
        deviceConnectFragment.arguments = bundle
        return deviceConnectFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_connect, container, false)
        mViewDataBinding.handlers = EventHandler()
        val bitmap = BlurImage.with(context).load(R.mipmap.lamp_category_bg).intensity(20f).imageBlur
        mViewDataBinding.clDeviceConnect.background = BitmapDrawable(resources, bitmap)
        return mViewDataBinding.root
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isReConnect = arguments.getBoolean("isReConnect", false)
        if (arguments.getBoolean("hasConnected")) {
            setFragment(DeviceConnectStep2Fragment().newInstance(arguments.getInt("categoryType"), isReConnect))
        } else {
            setFragment(DeviceConnectStep1Fragment().newInstance(arguments.getInt("categoryType")))
        }
    }

    private fun setFragment(frag: BaseFragment) {
        val transaction = childFragmentManager.beginTransaction()
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.replace(R.id.inner_frag_device_connect_container, frag, frag.javaClass.simpleName)
        transaction.addToBackStack(frag.javaClass.simpleName)
        transaction.commit()
    }


    override fun replaceFragment(containerId: Int, frag: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.push_right_in, R.anim.hold, R.anim.hold, R.anim.push_left_out)
        transaction.replace(containerId, frag, frag.javaClass.simpleName)
        transaction.addToBackStack(frag.javaClass.simpleName)
        transaction.commit()
    }

    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> (view.context as Activity).onBackPressed()
            }
        }
    }
}