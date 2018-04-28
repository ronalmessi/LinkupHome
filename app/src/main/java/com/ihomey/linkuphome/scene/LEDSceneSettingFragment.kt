package com.ihomey.linkuphome.scene

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentSceneSettingLedBinding
import com.ihomey.linkuphome.listener.IFragmentStackHolder

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class LEDSceneSettingFragment : BaseSceneSettingFragment() {

    private lateinit var mViewDataBinding: FragmentSceneSettingLedBinding

    fun newInstance(deviceId: Int, sceneMode: Int?): LEDSceneSettingFragment {
        val fragment = LEDSceneSettingFragment()
        val bundle = Bundle()
        bundle.putInt("deviceId", deviceId)
        if (sceneMode != null) bundle.putInt("sceneMode", sceneMode)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_setting_led, container, false)
        initController(3, arguments.getInt("deviceId", -1))
        mViewDataBinding.toolbarBack.setOnClickListener {
            if (parentFragment is IFragmentStackHolder) {
                val fsh = parentFragment as IFragmentStackHolder
                fsh.goBack()
            }
        }
        return mViewDataBinding.root
    }


    override fun onResume() {
        super.onResume()
        when (arguments.getInt("sceneMode", -1)) {
            0 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_spring_led)
            1 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rainforest_led)
            2 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_sunset_led)
            3 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_lighting_led)
        }
        mViewDataBinding.deviceRgpScene.setOnCheckedChangeListener(this)
    }

}