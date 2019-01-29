package com.ihomey.linkuphome.control

import android.databinding.DataBindingUtil
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentControlDownLightRgbBinding
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation

class BedControlRGBFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: FragmentControlDownLightRgbBinding

    fun newInstance(): BedControlRGBFragment {
        return BedControlRGBFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initController(8)
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_down_light_rgb, container, false)
        mViewDataBinding.handlers = ToolBarEventHandler()
        return mViewDataBinding.root
    }

    override fun updateViewData(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            mViewDataBinding.control = controlDevice
            mControlDevice = controlDevice
            if (mControlDevice != null) {
                mViewDataBinding.deviceColorRcv.setCurrentRadian(mControlDevice!!.state.colorPosition)
                if (mControlDevice!!.state.changeMode != -1) {
                    mViewDataBinding.deviceColorCbCycling.isChecked = true
                    mViewDataBinding.deviceCyclingSstgSpeed.visibility = View.VISIBLE
                    mViewDataBinding.deviceCyclingSstgSpeed.setCheckedAt(mControlDevice!!.state.changeMode, true)
                } else {
                    mViewDataBinding.deviceColorCbCycling.isChecked = false
                }
                mViewDataBinding.deviceColorCbLighting.isActivated = mControlDevice!!.state.light==1
            }
            mViewDataBinding.deviceColorRcv.setColorValueListener(this)
            mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
            mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
            mViewDataBinding.deviceColorCbLighting.setOnClickListener(this)
            mViewDataBinding.deviceColorCbCycling.setOnCheckedChangeListener(this)
            mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(this)
        }else{
            mViewDataBinding.control = null
            mControlDevice=null
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            try {
                updateViewData(mControlDevice)
            }catch (e:Exception){

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceColorRcv.setColorValueListener(null)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
        mViewDataBinding.deviceColorCbLighting.setOnClickListener(null)
        mViewDataBinding.deviceColorCbCycling.setOnCheckedChangeListener(null)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(null)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        super.onCheckedChanged(buttonView, isChecked)
        if (buttonView?.id == R.id.device_color_cb_cycling) {
            mViewDataBinding.deviceCyclingSstgSpeed.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            mViewDataBinding.deviceCyclingSstgSpeed.animation = if (isChecked) moveToViewLocationAnimation() else moveToViewBottomAnimation()
            if (isChecked) mViewDataBinding.deviceCyclingSstgSpeed.setCheckedAt(1, true)
            if (isChecked)  mViewDataBinding.deviceColorCbLighting.isActivated = false
        }
    }

    override fun onColorValueChange(time: Int) {
        super.onColorValueChange(time)
        mViewDataBinding.deviceColorCbLighting.isActivated = false
        mViewDataBinding.deviceColorCbCycling.isChecked = false
    }

    override fun onColorValueChanged(time: Int) {
        super.onColorValueChange(time)
        mViewDataBinding.deviceColorCbLighting.isActivated = false
        mViewDataBinding.deviceColorCbCycling.isChecked = false
    }

    override fun onClick(v: View) {
        super.onClick(v)
        if (v.id == R.id.device_color_cb_lighting) {
            mViewDataBinding.deviceColorCbCycling.isChecked = false
            mViewDataBinding.deviceColorCbLighting.isActivated = true
        }
    }
}