package com.ihomey.linkuphome.scene

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.widget.RadioGroupPlus

abstract class BaseSceneSettingFragment : BaseFragment(), RadioGroupPlus.OnCheckedChangeListener {

    protected lateinit var mControlDevice: SingleDevice
    private var controller: Controller? = null
    protected lateinit var viewModel: HomeActivityViewModel
    protected lateinit var listener: MeshServiceStateListener

    abstract fun updateViewData(singleDevice: SingleDevice)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(this, Observer<SingleDevice> {
            updateViewData(it)
        })
    }

    fun initController(deviceType: Int) {
        controller = ControllerFactory().createController(deviceType)
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
        if (listener.isMeshServiceConnected()) controller?.setLightScene(mControlDevice.id, sceneModeValue)
        mControlDevice.state?.sceneMode = sceneModeValue
        viewModel.updateDevice(mControlDevice)
    }
}