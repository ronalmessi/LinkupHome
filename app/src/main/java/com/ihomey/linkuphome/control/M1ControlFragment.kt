package com.ihomey.linkuphome.control

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.MixControlViewAdapter
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.databinding.M1ControlFragmentBinding
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation
import kotlinx.android.synthetic.main.m1_control_fragment.*

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class M1ControlFragment : BaseControlFragment(), RadioGroup.OnCheckedChangeListener , View.OnClickListener{

    private lateinit var mViewDataBinding: M1ControlFragmentBinding

    fun newInstance(): M1ControlFragment {
        return M1ControlFragment()
    }


    override fun getTitleView(): TextView {
        return  mViewDataBinding.tvTitle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.m1_control_fragment, container, false)
        mViewDataBinding.bleControlVp.offscreenPageLimit = 2
        mViewDataBinding.bleControlVp.adapter = MixControlViewAdapter(childFragmentManager)
        mViewDataBinding.rgControlSwitch.setOnCheckedChangeListener(this)
        mViewDataBinding.handlers = ToolBarEventHandler()
        mViewDataBinding.deviceSeekBarBrightness.max=85
        mViewDataBinding.root.setOnClickListener { hideCyclingSstgSpeedView() }
        mViewDataBinding.ivSetting.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_m1ControlFragment_to_m1ControlSettingFragment) }
        return mViewDataBinding.root
    }

    override fun updateViewData(device: Device) {
        mViewDataBinding.control = device
        mControlDevice = device
        btn_device_environmental_indicators.visibility=if(device.roomId==1) View.VISIBLE else View.GONE
        mViewDataBinding.deviceStateCbPower.isChecked=(device.parameters?.on==1)
        device.parameters?.brightness?.let { mViewDataBinding.deviceSeekBarBrightness.progress=it}
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
        mViewDataBinding.btnDeviceCycling.setOnClickListener(this)
        mViewDataBinding.btnDeviceEnvironmentalIndicators.setOnClickListener(this)
        mViewDataBinding.btnDeviceLighting.setOnClickListener(this)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.btnDeviceCycling.setOnClickListener(null)
        mViewDataBinding.btnDeviceLighting.setOnClickListener(null)
        mViewDataBinding.btnDeviceEnvironmentalIndicators.setOnClickListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(null)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_device_cycling) {
            val isVisible = mViewDataBinding.deviceCyclingSstgSpeed.visibility == View.VISIBLE
            mViewDataBinding.deviceCyclingSstgSpeed.visibility = if (isVisible) View.GONE else View.VISIBLE
            mViewDataBinding.deviceCyclingSstgSpeed.animation = if (!isVisible) moveToViewLocationAnimation() else moveToViewBottomAnimation()
        }else if(v.id == R.id.btn_device_lighting){
            if (mViewDataBinding.deviceCyclingSstgSpeed.visibility == View.VISIBLE)hideCyclingSstgSpeedView()
            mViewDataBinding.handlers?.onClick(v)
        } else if(v.id == R.id.btn_device_environmental_indicators){
            Navigation.findNavController(v).navigate(R.id.action_m1ControlFragment_to_environmentalIndicatorsFragment)
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if(checkedId==R.id.rb_control_rgb){
            mViewDataBinding.bleControlVp.setCurrentItem(0,true)
        }else if(checkedId==R.id.rb_control_warm_cold){
            mViewDataBinding.bleControlVp.setCurrentItem(1,true)
        }

    }

    fun hideCyclingSstgSpeedView(){
        mViewDataBinding.deviceCyclingSstgSpeed.visibility =View.GONE
        mViewDataBinding.deviceCyclingSstgSpeed.animation = moveToViewBottomAnimation()
    }
}