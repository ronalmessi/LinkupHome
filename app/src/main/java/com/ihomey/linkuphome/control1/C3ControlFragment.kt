package com.ihomey.linkuphome.control1

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SingleDevice
import com.ihomey.linkuphome.databinding.C3ControlFragmentBinding
import com.ihomey.linkuphome.listeners.BatteryValueListener
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class C3ControlFragment : BaseControlFragment(), BatteryValueListener {

    private lateinit var mViewDataBinding: C3ControlFragmentBinding

    fun newInstance(): C3ControlFragment {
        return C3ControlFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.c3_control_fragment, container, false)
        initController(4)
        mViewDataBinding.handlers = ToolBarEventHandler()
        return mViewDataBinding.root
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (!userVisibleHint) {
//            try {
////                mViewDataBinding.deviceIvBattery.visibility = View.INVISIBLE
//            } catch (e: Exception) {
//                Log.d("LinkupHome", "you should firstly init ViewDataBinding!")
//            }
//        } else {
//            if (mControlDevice != null) {
//                listener.getBatteryState(mControlDevice?.id!!, this)
//            }
//        }
//    }

    override fun updateViewData(singleDevice: SingleDevice) {
        mViewDataBinding.control = singleDevice
        mControlDevice = singleDevice
        mViewDataBinding.deviceColorRgbCv.currentRadian = mControlDevice.state?.colorPosition!!
        mViewDataBinding.deviceColorRgbCv.setColorValueListener(this)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
        mViewDataBinding.btnDeviceCycling.setOnClickListener(this)
        mViewDataBinding.btnDeviceLighting.setOnClickListener(this)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        mViewModel?.getBridgeState()?.observe(this, Observer<Boolean> {
//            if (it != null && it) {
//                if (mControlDevice != null) {
//                    listener.getBatteryState(mControlDevice?.id!!, this)
//                }
//            }
//        })
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


    override fun onBatteryLevelReceived(deviceId: Int, batteryValue: Int) {
        if (deviceId > 32768) {
//            mViewDataBinding.deviceIvBattery.visibility = View.VISIBLE
//            mViewDataBinding.deviceIvBattery.setImageResource(batteryIcons[Math.ceil(batteryValue / 20.0).toInt()])
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_device_cycling) {
            val isVisible = mViewDataBinding.deviceCyclingSstgSpeed.visibility == View.VISIBLE
            mViewDataBinding.deviceCyclingSstgSpeed.visibility = if (isVisible) View.GONE else View.VISIBLE
            mViewDataBinding.deviceCyclingSstgSpeed.animation = if (!isVisible) moveToViewLocationAnimation() else moveToViewBottomAnimation()
        }
    }
}