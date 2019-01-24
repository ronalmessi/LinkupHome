package com.ihomey.linkuphome.control

import androidx.databinding.DataBindingUtil
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
class S2ControlFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: FragmentControlWarmColdBinding

    fun newInstance(): S2ControlFragment {
        return S2ControlFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_warm_cold, container, false)
        initController(6)
        mTopRightMenu.addMenuList(arrayListOf(MenuItem(R.mipmap.toolbar_menu_rename, R.mipmap.toolbar_menu_rename_select, R.string.menu_rename), MenuItem(R.mipmap.toolbar_menu_alarm, R.mipmap.toolbar_menu_alarm_select, R.string.menu_timer), MenuItem(R.mipmap.toolbar_menu_scan, R.mipmap.toolbar_menu_scan_select, R.string.menu_scan), MenuItem(R.mipmap.toolbar_menu_share, R.mipmap.toolbar_menu_share_select, R.string.menu_control_share)))
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
        }else{
            mViewDataBinding.control = null
            mControlDevice=null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.deviceDbvColorTemperature.setColorTemperatureListener(null)
        mViewDataBinding.deviceSeekBarBrightness.setOnSeekBarChangeListener(null)
        mViewDataBinding.deviceStateCbPower.setOnCheckedChangeListener(null)
    }
}