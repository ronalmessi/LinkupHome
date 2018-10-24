package com.ihomey.linkuphome.scene

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.ihomey.linkuphome.CODE_LIGHT_COLORS
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.control.BaseControlFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.widget.RadioGroupPlus

abstract class BaseSceneSettingFragment : BaseControlFragment(), RadioGroupPlus.OnCheckedChangeListener {

    private var controller: Controller? = null
    var sceneSettingViewModel: SceneSettingViewModel? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sceneSettingViewModel = ViewModelProviders.of(activity).get(SceneSettingViewModel::class.java)
    }

}