package com.ihomey.linkuphome.scene

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentSceneSettingRgbBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class RGBSceneSettingFragment : BaseSceneSettingFragment() {

    lateinit var mViewDataBinding: FragmentSceneSettingRgbBinding

    fun newInstance(): RGBSceneSettingFragment {
        return RGBSceneSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_setting_rgb, container, false)
        initController(1)
        mViewDataBinding.toolbarBack.setOnClickListener {
            activity.onBackPressed()
        }
        return mViewDataBinding.root
    }

    override fun updateViewData(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            mViewDataBinding.control = controlDevice
            mControlDevice = controlDevice
            if (mControlDevice != null) {
                when (mControlDevice?.state?.sceneMode) {
                    0 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_read_rgb)
                    1 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_sunset_rgb)
                    2 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rest_rgb)
                    3 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_spring_rgb)
                    4 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rainforest_rgb)
                }
            }
            mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(this)
        } else {
            mViewDataBinding.control = null
            mControlDevice = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(null)
    }
}