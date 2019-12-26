package com.ihomey.linkuphome.devicecontrol.switchtimer

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
import com.ihomey.linkuphome.devicecontrol.scene.SceneSettingViewModel
import com.ihomey.linkuphome.devicecontrol.switchtimer.timersetting.TimerSettingAdapter
import com.ihomey.linkuphome.devicecontrol.view.ControlViewFactory
import com.ihomey.linkuphome.home.HomeActivityViewModel
import kotlinx.android.synthetic.main.control_switch_timer_fragment.*

class SwitchTimerControlFragment : BaseFragment(), OnTimerChangedListener {

    private var mControlDevice: Device? = null
    private lateinit var mViewModel: HomeActivityViewModel
    private lateinit var viewModel: SceneSettingViewModel
    private lateinit var contentView: View
    private var controlView: BaseSwitchTimerControlView? = null

    private var isDataLoaded:Boolean=false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.control_switch_timer_fragment, container, false)
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.offscreenPageLimit = 2
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        rg_timer_setting.setOnCheckedChangeListener { _, checkedId ->
            viewPager.currentItem = if (checkedId == R.id.rb_timer_setting_on) 0 else 1
        }
        rg_timer_setting.check(R.id.rb_timer_setting_on)
        iv_back.setOnClickListener { Navigation.findNavController(view).popBackStack() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(HomeActivityViewModel::class.java)
            mViewModel.getCurrentControlDevice().observe(this, Observer<Device> { it0 ->
                mControlDevice = it0
                viewModel.setCurrentDeviceId(it0.id)
                controlView = ControlViewFactory().createSwitchTimerControlView(contentView, it0)
                controlView?.setOnTimerChangedListener(this)
                viewPager.adapter = controlView?.let { TimerSettingAdapter(childFragmentManager, it,mControlDevice?.type) }
            })
        }
        viewModel = ViewModelProviders.of(this).get(SceneSettingViewModel::class.java)
        viewModel.mCurrentLocalState.observe(viewLifecycleOwner, Observer<Resource<LocalState>> {
            if (it.status == Status.SUCCESS) {
              if(!isDataLoaded){
                  controlView?.bindTo(it.data)
                  isDataLoaded=true
              }
            }
        })
    }

    override fun onTimerChanged(localState: LocalState) {
        mControlDevice?.let {
            localState.id = it.id
            viewModel.updateLocalState(localState)
        }
    }
}