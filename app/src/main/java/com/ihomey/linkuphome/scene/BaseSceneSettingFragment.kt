package com.ihomey.linkuphome.scene

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.widget.RadioGroupPlus

abstract class BaseSceneSettingFragment : BaseFragment(), RadioGroupPlus.OnCheckedChangeListener {

    protected lateinit var mControlDevice: SingleDevice
    private var mLocalState: LocalState? = null
    private var controller: Controller? = null
    protected lateinit var viewModel: HomeActivityViewModel
    protected lateinit var mViewModel: SceneSettingViewModel
    protected lateinit var listener: MeshServiceStateListener

    abstract fun updateViewData(localState: LocalState?)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel = ViewModelProviders.of(this).get(SceneSettingViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(this, Observer<SingleDevice> {
            mControlDevice = it
            mViewModel.setCurrentDeviceId(it.id)
        })
        mViewModel.mCurrentLocalState.observe(this, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS) {
                mLocalState = it.data
                updateViewData(it.data)
            }
        })
    }

    fun initController(deviceType: Int) {
        controller = ControllerFactory().createController(deviceType + 1)
    }

    override fun onCheckedChanged(group: RadioGroupPlus?, checkedId: Int) {
        var sceneModeValue = -1
        when (checkedId) {
            R.id.rb_scene_read_rgb, R.id.rb_scene_spring_n1, R.id.rb_scene_flow -> sceneModeValue = 0
            R.id.rb_scene_sunset_rgb, R.id.rb_scene_rainforest_n1, R.id.rb_scene_star -> sceneModeValue = 1
            R.id.rb_scene_rest_rgb, R.id.rb_scene_sunset_n1, R.id.rb_scene_rainbow -> sceneModeValue = 2
            R.id.rb_scene_spring_rgb, R.id.rb_scene_lighting_n1, R.id.rb_scene_surf -> sceneModeValue = 3
            R.id.rb_scene_rainforest_rgb, R.id.rb_scene_seek -> sceneModeValue = 4
        }
        if (listener.isMeshServiceConnected()) controller?.setLightScene(mControlDevice.instructId, sceneModeValue)
        mControlDevice.let {
            if (mLocalState == null) mLocalState = LocalState(it.id)
            mLocalState?.let { it.sceneMode = sceneModeValue
                mViewModel.updateLocalState(it)
            }
        }
    }
}