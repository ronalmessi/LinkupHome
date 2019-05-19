package com.ihomey.linkuphome.scene

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.databinding.N1SceneSettingFragmentBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class N1SceneSettingFragment : BaseSceneSettingFragment() {

    lateinit var mViewDataBinding: N1SceneSettingFragmentBinding

    companion object {
        fun newInstance() = N1SceneSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.n1_scene_setting_fragment, container, false)
        mViewDataBinding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        return mViewDataBinding.root
    }

    override fun updateViewData(localState: LocalState?) {
        when (localState?.sceneMode) {
            0 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_spring_n1)
            1 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rainforest_n1)
            2 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_sunset_n1)
            3 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_lighting_n1)
        }
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(null)
    }

}