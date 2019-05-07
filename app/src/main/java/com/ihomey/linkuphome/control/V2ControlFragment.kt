package com.ihomey.linkuphome.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.databinding.R2ControlFragmentBinding
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class V2ControlFragment : BaseControlFragment(), View.OnClickListener {

    private lateinit var mViewDataBinding: R2ControlFragmentBinding

    fun newInstance(): V2ControlFragment {
        return V2ControlFragment()
    }


    override fun getTitleView(): TextView {
        return  mViewDataBinding.tvTitle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.r2_control_fragment, container, false)
        initController(9)
        mViewDataBinding.handlers = ToolBarEventHandler()
        mViewDataBinding.deviceSeekBarBrightness.max=22
        mViewDataBinding.root.setOnClickListener {
            mViewDataBinding.deviceCyclingSstgSpeed.visibility =View.GONE
            mViewDataBinding.deviceCyclingSstgSpeed.animation = moveToViewBottomAnimation()
        }
        return mViewDataBinding.root
    }

    override fun updateViewData(device: Device) {
        mViewDataBinding.control = device
        mControlDevice = device
        mViewDataBinding.deviceStateCbPower.isChecked=(device.parameters?.on==1)
        device.parameters?.brightness?.let { mViewDataBinding.deviceSeekBarBrightness.progress=it}
//        mViewDataBinding.deviceColorRgbCv.currentRadian = mControlDevice.state.colorPosition
        mViewDataBinding.deviceColorRgbCv.setColorValueListener(this)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
        mViewDataBinding.btnDeviceCycling.setOnClickListener(this)
        mViewDataBinding.btnDeviceLighting.setOnClickListener(this)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceColorRgbCv.setColorValueListener(null)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.btnDeviceCycling.setOnClickListener(null)
        mViewDataBinding.btnDeviceLighting.setOnClickListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(null)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_device_cycling) {
            val isVisible = mViewDataBinding.deviceCyclingSstgSpeed.visibility == View.VISIBLE
            mViewDataBinding.deviceCyclingSstgSpeed.visibility = if (isVisible) View.GONE else View.VISIBLE
            mViewDataBinding.deviceCyclingSstgSpeed.animation = if (!isVisible) moveToViewLocationAnimation() else moveToViewBottomAnimation()
        }else if(v.id == R.id.btn_device_lighting){
            if (mViewDataBinding.deviceCyclingSstgSpeed.visibility == View.VISIBLE){
                mViewDataBinding.deviceCyclingSstgSpeed.visibility = View.GONE
                mViewDataBinding.deviceCyclingSstgSpeed.animation =moveToViewBottomAnimation()
            }
            mViewDataBinding.handlers?.onClick(v)
        }
    }
}