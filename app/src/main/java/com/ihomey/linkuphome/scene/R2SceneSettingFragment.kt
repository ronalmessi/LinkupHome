package com.ihomey.linkuphome.scene

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.databinding.R2SceneSettingFragmentBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class R2SceneSettingFragment : BaseSceneSettingFragment() {

    lateinit var mViewDataBinding: R2SceneSettingFragmentBinding

    companion object {
        fun newInstance() = R2SceneSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.r2_scene_setting_fragment, container, false)
        mViewDataBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        return mViewDataBinding.root
    }

    override fun updateViewData(localState: LocalState?) {
        when (localState?.sceneMode) {
            0 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_read_rgb)
            1 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_sunset_rgb)
            2 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rest_rgb)
            3 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_spring_rgb)
            4 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rainforest_rgb)
        }
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(null)
    }
}