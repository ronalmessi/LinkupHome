package com.ihomey.linkuphome.scene

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentSceneSettingMiniOutdoorBinding
import com.ihomey.linkuphome.databinding.FragmentSceneSettingRgbBinding

/**
 * Created by dongcaizheng on 2018/4/15.
 */
class MiniOutdoorSceneSettingFragment : BaseSceneSettingFragment() {

    lateinit var mViewDataBinding: FragmentSceneSettingMiniOutdoorBinding

    fun newInstance(deviceId: Int, sceneMode: Int?): MiniOutdoorSceneSettingFragment {
        val fragment = MiniOutdoorSceneSettingFragment()
        val bundle = Bundle()
        bundle.putInt("deviceId", deviceId)
        if (sceneMode != null) bundle.putInt("sceneMode", sceneMode)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_setting_mini_outdoor, container, false)
        initController(4, arguments.getInt("deviceId", -1))
        mViewDataBinding.toolbarBack.setOnClickListener {
            activity.onBackPressed()
        }
        return mViewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        mViewDataBinding.rbScene.isChecked = arguments.getInt("sceneMode", -1) == 1
        mViewDataBinding.tvDeviceSceneName.isSelected = arguments.getInt("sceneMode", -1) == 1
        mViewDataBinding.llDeviceScene.setOnClickListener {
            val isChecked = mViewDataBinding.rbScene.isChecked
            mViewDataBinding.rbScene.isChecked = !isChecked
            mViewDataBinding.tvDeviceSceneName.isSelected = !isChecked
            if (listener.isMeshServiceConnected() && mDeviceId != -1) {
                if (mDeviceId != -1) {
                    controller?.setLightScene(mDeviceId, if (mViewDataBinding.rbScene.isChecked) 1 else 0)
                }
                mViewModel?.updateDeviceSceneMode(4, mDeviceId, if (mViewDataBinding.rbScene.isChecked) 1 else 0)
            }
        }
    }
}