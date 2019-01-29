package com.ihomey.linkuphome.scene

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentSceneSettingRgbBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class S1SceneSettingFragment : BaseSceneSettingFragment() {

    lateinit var mViewDataBinding: FragmentSceneSettingRgbBinding

    fun newInstance(deviceId: Int, sceneMode: Int?): S1SceneSettingFragment {
        val fragment = S1SceneSettingFragment()
        val bundle = Bundle()
        bundle.putInt("deviceId", deviceId)
        if (sceneMode != null) bundle.putInt("sceneMode", sceneMode)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_setting_rgb, container, false)
        initController(6, arguments.getInt("deviceId", -1))
        mViewDataBinding.toolbarBack.setOnClickListener {
            activity.onBackPressed()
        }
        return mViewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        when(arguments.getInt("sceneMode", -1)){
            0->mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_read_rgb)
            1->mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_sunset_rgb)
            2->mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rest_rgb)
            3->mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_spring_rgb)
            4->mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rainforest_rgb)
        }
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(this)
    }
}