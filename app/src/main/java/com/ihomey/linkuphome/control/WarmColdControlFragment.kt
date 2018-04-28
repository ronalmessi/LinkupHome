package com.ihomey.linkuphome.control

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentControlWarmColdBinding
import com.ihomey.linkuphome.widget.toprightmenu.MenuItem

/**
 * Created by dongcaizheng on 2018/4/10.
 */
class WarmColdControlFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: FragmentControlWarmColdBinding

    fun newInstance(): WarmColdControlFragment {
        return WarmColdControlFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_warm_cold, container, false)
        initController(2)
        mTopRightMenu.addMenuList(arrayListOf(MenuItem(R.mipmap.toolbar_menu_rename, R.mipmap.toolbar_menu_rename_select, R.string.rename), MenuItem(R.mipmap.toolbar_menu_alarm, R.mipmap.toolbar_menu_alarm_select, R.string.timer), MenuItem(R.mipmap.toolbar_menu_scan, R.mipmap.toolbar_menu_scan_select, R.string.scan), MenuItem(R.mipmap.toolbar_menu_share, R.mipmap.toolbar_menu_share_select, R.string.shareOperation)))
        mViewDataBinding.handlers = ToolBarEventHandler()
        return mViewDataBinding.root
    }

    override fun updateViewData(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            mViewDataBinding.control = controlDevice
            mControlDevice = controlDevice
            if (mControlDevice != null) {
                mViewDataBinding.deviceDbvColorTemperature.currentColorTemperature = mControlDevice!!.state.colorTemperature
            }
            mViewDataBinding.deviceDbvColorTemperature.setColorTemperatureListener(this)
            mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(this)
            mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceDbvColorTemperature.setColorTemperatureListener(null)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
    }
}