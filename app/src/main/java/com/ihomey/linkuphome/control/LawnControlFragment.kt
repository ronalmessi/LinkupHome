package com.ihomey.linkuphome.control

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.batteryIcons
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentControlLawnBinding
import com.ihomey.linkuphome.listeners.BatteryValueListener
import com.ihomey.linkuphome.moveToViewBottomAnimation
import com.ihomey.linkuphome.moveToViewLocationAnimation
import com.ihomey.linkuphome.widget.toprightmenu.MenuItem

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class LawnControlFragment : BaseControlFragment(), BatteryValueListener {

    private lateinit var mViewDataBinding: FragmentControlLawnBinding

    fun newInstance(): LawnControlFragment {
        return LawnControlFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_lawn, container, false)
        initController(0)
        mTopRightMenu.addMenuList(arrayListOf(MenuItem(R.mipmap.toolbar_menu_rename, R.mipmap.toolbar_menu_rename_select, R.string.menu_rename), MenuItem(R.mipmap.toolbar_menu_scan, R.mipmap.toolbar_menu_scan_select, R.string.menu_scan), MenuItem(R.mipmap.toolbar_menu_share, R.mipmap.toolbar_menu_share_select, R.string.menu_control_share)))
        mViewDataBinding.handlers = ToolBarEventHandler()

        return mViewDataBinding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!userVisibleHint) {
            try {
                mViewDataBinding.deviceIvBattery.visibility = View.INVISIBLE
            } catch (e: Exception) {
                Log.d("LinkupHome", "you should firstly init ViewDataBinding!")
            }
        } else {
            if (mControlDevice != null) {
                listener.getBatteryState(mControlDevice?.id!!, this)
            }
        }
    }

    override fun updateViewData(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            mViewDataBinding.control = controlDevice
            listener.getBatteryState(controlDevice.id, this)
            mControlDevice = controlDevice
            if (mControlDevice != null) {
                mViewDataBinding.deviceIvBattery.visibility = View.INVISIBLE
                mViewDataBinding.deviceColorRgbCv.currentRadian = mControlDevice!!.state.colorPosition
                if (mControlDevice!!.state.changeMode != -1) {
                    mViewDataBinding.deviceColorCbCycling.isChecked = true
                    mViewDataBinding.deviceCyclingSstgSpeed.visibility = View.VISIBLE
                    mViewDataBinding.deviceCyclingSstgSpeed.setCheckedAt(mControlDevice!!.state.changeMode, true)
                } else {
                    mViewDataBinding.deviceColorCbCycling.isChecked = false
                }
                mViewDataBinding.deviceColorCbLighting.isActivated = mControlDevice!!.state.light == 1
            }
            mViewDataBinding.deviceColorRgbCv.setColorValueListener(this)
            mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
            mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
            mViewDataBinding.deviceColorCbCycling.setOnCheckedChangeListener(this)
            mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(this)
            mViewDataBinding.deviceColorCbLighting.setOnClickListener(this)
        } else {
            mViewDataBinding.control = null
            mViewDataBinding.deviceIvBattery.visibility = View.INVISIBLE
            mControlDevice = null
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel?.isBridgeStateChanged()?.observe(this, Observer<Void> {
            if (mControlDevice != null) {
                listener.getBatteryState(mControlDevice?.id!!, this)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceColorRgbCv.setColorValueListener(null)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
        mViewDataBinding.deviceColorCbLighting.setOnClickListener(null)
        mViewDataBinding.deviceColorCbCycling.setOnCheckedChangeListener(null)
        mViewDataBinding.deviceCyclingSstgSpeed.setOnCheckedChangeListener(null)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        super.onCheckedChanged(buttonView, isChecked)
        if (buttonView?.id == R.id.device_color_cb_cycling) {
            mViewDataBinding.deviceCyclingSstgSpeed.visibility = if (isChecked) View.VISIBLE else View.GONE
            mViewDataBinding.deviceCyclingSstgSpeed.animation = if (isChecked) moveToViewLocationAnimation() else moveToViewBottomAnimation()
            if (isChecked) mViewDataBinding.deviceCyclingSstgSpeed.setCheckedAt(1, true)
            if (isChecked) mViewDataBinding.deviceColorCbLighting.isActivated = false
        }
    }

    override fun onBatteryLevelReceived(deviceId: Int, batteryValue: Int) {
        if (deviceId > 32768) {
            mViewDataBinding.deviceIvBattery.visibility = View.VISIBLE
            mViewDataBinding.deviceIvBattery.setImageResource(batteryIcons[Math.ceil(batteryValue / 20.0).toInt()])
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