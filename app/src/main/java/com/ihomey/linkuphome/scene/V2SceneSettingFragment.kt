package com.ihomey.linkuphome.scene

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.databinding.V2SceneSettingFragmentBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class V2SceneSettingFragment : BaseSceneSettingFragment() {

    lateinit var mViewDataBinding: V2SceneSettingFragmentBinding

    companion object {
        fun newInstance() = V2SceneSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.v2_scene_setting_fragment, container, false)
        initController(9)
        mViewDataBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        return mViewDataBinding.root
    }

    override fun updateViewData(singleDevice: SingleDevice) {
        mControlDevice = singleDevice
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(null)
    }

}