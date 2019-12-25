package com.ihomey.linkuphome.devicecontrol.scene

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.LocalState
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.devicecontrol.controller.SceneController
import com.ihomey.linkuphome.devicecontrol.view.ControlViewFactory
import com.ihomey.linkuphome.home.HomeActivityViewModel
import kotlinx.android.synthetic.main.device_control_fragment.*

class SceneControlFragment : BaseFragment(), OnSceneChangedListener {

    private var mControlDevice: Device? = null
    private var mLocalState: LocalState? = null
    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var viewModel: SceneSettingViewModel

    private var controller: SceneController? = null

    private var controlView: BaseSceneControlView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.control_scene_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(view).popBackStack() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(HomeActivityViewModel::class.java)
            mViewModel.getCurrentControlDevice().observe(this, Observer<Device> { it0 ->
                mControlDevice = it0
                it0?.let { controller = LightControllerFactory().createColorSceneController(it) }
                viewModel.setCurrentDeviceId(it0.id)
                context?.let { it1 ->
                    controlView = ControlViewFactory().createSceneControlView(it0.type, it1)
                    controlView?.let {
                        it.setOnSceneChangListener(this)
                        it.getControlView().setBackgroundColor(Color.parseColor("#F2F2F2"))
                        rootView.addView(it.getControlView(), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                    }
                }
            })
        }
        viewModel = ViewModelProviders.of(this).get(SceneSettingViewModel::class.java)
        viewModel.mCurrentLocalState.observe(viewLifecycleOwner, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS) {
                mLocalState = it.data
                controlView?.bindTo(mLocalState)
            }
        })
    }

    override fun onSceneChanged(sceneValue: Int) {
        controller?.setScene(sceneValue)
        mControlDevice?.let {
            if (mLocalState == null) mLocalState = LocalState(it.id)
            mLocalState?.let {
                it.sceneMode = sceneValue
                viewModel.updateLocalState(it)
            }
        }

    }

}