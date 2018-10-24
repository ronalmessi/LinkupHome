package com.ihomey.linkuphome.scene

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentSceneSettingLedBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class LEDSceneSettingFragment : BaseSceneSettingFragment() {

    private lateinit var mViewDataBinding: FragmentSceneSettingLedBinding

    fun newInstance(deviceType: Int): LEDSceneSettingFragment {
        val fragment = LEDSceneSettingFragment()
        val bundle = Bundle()
        bundle.putInt("deviceType", deviceType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_setting_led, container, false)
        initController(arguments.getInt("deviceType", -1))
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
                when (arguments.getInt("sceneMode", -1)) {
                    0 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_spring_led)
                    1 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_rainforest_led)
                    2 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_sunset_led)
                    3 -> mViewDataBinding.deviceRgpScene.check(R.id.rb_scene_lighting_led)
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