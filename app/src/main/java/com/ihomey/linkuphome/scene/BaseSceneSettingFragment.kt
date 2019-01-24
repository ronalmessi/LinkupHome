package com.ihomey.linkuphome.scene

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.widget.RadioGroupPlus

abstract class BaseSceneSettingFragment : BaseFragment(), RadioGroupPlus.OnCheckedChangeListener {

     var mDeviceId: Int = -1
    private var mDeviceType: Int = -1
     var controller: Controller? = null
    protected var mViewModel: SceneSettingViewModel? = null
    protected lateinit var listener: MeshServiceStateListener


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(SceneSettingViewModel::class.java)
    }

    fun initController(deviceType: Int, deviceId: Int) {
        controller = ControllerFactory().createController(deviceType)
        mDeviceId = deviceId
        mDeviceType = deviceType
    }

    override fun onCheckedChanged(group: RadioGroupPlus?, checkedId: Int) {
        var sceneModeValue = -1
        when (checkedId) {
            R.id.rb_scene_read_rgb, R.id.rb_scene_spring_led -> sceneModeValue = 0
            R.id.rb_scene_sunset_rgb, R.id.rb_scene_rainforest_led -> sceneModeValue = 1
            R.id.rb_scene_rest_rgb, R.id.rb_scene_sunset_led -> sceneModeValue = 2
            R.id.rb_scene_spring_rgb, R.id.rb_scene_lighting_led -> sceneModeValue = 3
            R.id.rb_scene_rainforest_rgb -> sceneModeValue = 4
        }
        if (listener.isMeshServiceConnected() && mDeviceId != -1 && sceneModeValue != -1) {
            if (mDeviceId != -1 && sceneModeValue != -1) {
                controller?.setLightScene(mDeviceId, sceneModeValue)
            }
            if (sceneModeValue != -1) {
                mViewModel?.updateDeviceSceneMode(mDeviceType, mDeviceId, sceneModeValue)
            }
        }
    }

}