package com.ihomey.linkuphome.bed

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentThermometerBinding
import com.ihomey.linkuphome.main.BleLampFragment


/**
 * Created by dongcaizheng on 2017/12/27.
 */
class ThermometerFragment : BaseFragment() {

    private lateinit var mViewDataBinding: FragmentThermometerBinding

    fun newInstance(deviceId: Int): ThermometerFragment {
        val fragment = ThermometerFragment()
        val bundle = Bundle()
        bundle.putInt("deviceId", deviceId)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_thermometer, container, false)
        val bleLampFragment = parentFragment.parentFragment as BleLampFragment
        mViewDataBinding.toolbarOpenDrawer.setOnClickListener {
            bleLampFragment.openDrawer()
        }
        return mViewDataBinding.root
    }
}