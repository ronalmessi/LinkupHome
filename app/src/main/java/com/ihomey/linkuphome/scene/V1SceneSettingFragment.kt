package com.ihomey.linkuphome.scene

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.databinding.V1SceneSettingFragmentBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class V1SceneSettingFragment : BaseSceneSettingFragment() {

    lateinit var mViewDataBinding: V1SceneSettingFragmentBinding

    companion object {
        fun newInstance() = V1SceneSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.v1_scene_setting_fragment, container, false)
        initController(5)
        mViewDataBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        return mViewDataBinding.root
    }

    override fun updateViewData(singleDevice: SingleDevice) {
        mControlDevice = singleDevice
        when (singleDevice.state.sceneMode) {
            1 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_flow_rgb)
        }
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(null)
    }

}