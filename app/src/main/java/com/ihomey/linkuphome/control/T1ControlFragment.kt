package com.ihomey.linkuphome.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.T1ControlViewAdapter
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.databinding.T1ControlFragmentBinding
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class T1ControlFragment : BaseControlFragment(), RadioGroup.OnCheckedChangeListener , View.OnClickListener{

    private lateinit var mViewDataBinding: T1ControlFragmentBinding

    fun newInstance(): T1ControlFragment {
        return T1ControlFragment()
    }


    override fun getTitleView(): TextView {
        return  mViewDataBinding.tvTitle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.t1_control_fragment, container, false)
        mViewDataBinding.bleControlVp.offscreenPageLimit = 2
        mViewDataBinding.bleControlVp.adapter = T1ControlViewAdapter(childFragmentManager)
        mViewDataBinding.rgControlSwitch.setOnCheckedChangeListener(this)
        initController(8)
        mViewDataBinding.handlers = ToolBarEventHandler()
        return mViewDataBinding.root
    }

    override fun updateViewData(singleDevice: SingleDevice) {
        mViewDataBinding.control = singleDevice
        mControlDevice = singleDevice
        mViewDataBinding.deviceStateCbPower.isChecked=(singleDevice.parameters?.on==1)
        singleDevice.parameters?.brightness?.let { mViewDataBinding.deviceSeekBarBrightness.progress=it}
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
        mViewDataBinding.btnDeviceCycling.setOnClickListener(this)
        mViewDataBinding.btnDeviceLighting.setOnClickListener(this)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if(checkedId==R.id.rb_control_rgb){
            mViewDataBinding.bleControlVp.currentItem=0
        }else if(checkedId==R.id.rb_control_warm_cold){
            mViewDataBinding.bleControlVp.currentItem=1
        }
    }
}