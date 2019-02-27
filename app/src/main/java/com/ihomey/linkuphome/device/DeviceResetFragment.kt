package com.ihomey.linkuphome.device

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.databinding.FragmentDeviceConnectedResetBinding
import com.ihomey.linkuphome.databinding.FragmentDeviceResetBinding


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class DeviceResetFragment : BaseFragment() {

    lateinit var mViewDataBinding: ViewDataBinding

    fun newInstance(categoryType: Int): DeviceResetFragment {
        val deviceResetFragment = DeviceResetFragment()
        val bundle = Bundle()
        bundle.putInt("categoryType", categoryType)
        deviceResetFragment.arguments = bundle
        return deviceResetFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val categoryType=arguments?.getInt("categoryType", 0)
        val hasConnected by PreferenceHelper("hasConnected$categoryType", false)
        if(hasConnected){
            mViewDataBinding=DataBindingUtil.inflate<FragmentDeviceConnectedResetBinding>(inflater, R.layout.fragment_device_connected_reset,container, false)
            (mViewDataBinding as FragmentDeviceConnectedResetBinding).handlers = EventHandler()
            val currentLanguage = LocaleHelper.getLanguage(context)
            when (currentLanguage) {
                "zh" -> (mViewDataBinding as FragmentDeviceConnectedResetBinding).ivDeviceResetGuide1.setImageResource(R.mipmap.ic_device_reset_guide1_zh)
                "de" -> (mViewDataBinding as FragmentDeviceConnectedResetBinding).ivDeviceResetGuide1.setImageResource(R.mipmap.ic_device_reset_guide1_de)
                "es" -> (mViewDataBinding as FragmentDeviceConnectedResetBinding).ivDeviceResetGuide1.setImageResource(R.mipmap.ic_device_reset_guide1_es)
                "fr" -> (mViewDataBinding as FragmentDeviceConnectedResetBinding).ivDeviceResetGuide1.setImageResource(R.mipmap.ic_device_reset_guide1_fr)
                "nl" -> (mViewDataBinding as FragmentDeviceConnectedResetBinding).ivDeviceResetGuide1.setImageResource(R.mipmap.ic_device_reset_guide1_nl)
                else -> (mViewDataBinding as FragmentDeviceConnectedResetBinding).ivDeviceResetGuide1.setImageResource(R.mipmap.ic_device_reset_guide1_en)
            }
        }else{
            mViewDataBinding=DataBindingUtil.inflate<FragmentDeviceResetBinding>(inflater, R.layout.fragment_device_reset, container, false)
            (mViewDataBinding as FragmentDeviceResetBinding).handlers = EventHandler()
        }
        return mViewDataBinding.root
    }

    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> (view.context as Activity).onBackPressed()
            }
        }
    }
}