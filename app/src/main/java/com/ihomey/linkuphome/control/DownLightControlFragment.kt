package com.ihomey.linkuphome.control

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DownLightControlPageAdapter
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentControlDownLightBinding
import com.ihomey.linkuphome.widget.toprightmenu.MenuItem

class DownLightControlFragment : BaseControlFragment(), RadioGroup.OnCheckedChangeListener {

    private lateinit var mViewDataBinding: FragmentControlDownLightBinding

    fun newInstance(): DownLightControlFragment {
        return DownLightControlFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_down_light, container, false)
        initController(8)
        mTopRightMenu.addMenuList(arrayListOf(MenuItem(R.mipmap.toolbar_menu_rename, R.mipmap.toolbar_menu_rename_select, R.string.menu_rename), MenuItem(R.mipmap.toolbar_menu_alarm, R.mipmap.toolbar_menu_alarm_select, R.string.menu_timer), MenuItem(R.mipmap.toolbar_menu_scan, R.mipmap.toolbar_menu_scan_select, R.string.menu_scan), MenuItem(R.mipmap.toolbar_menu_share, R.mipmap.toolbar_menu_share_select, R.string.menu_control_share)))
        mViewDataBinding.rgControlSwitch.setOnCheckedChangeListener(this)
        mViewDataBinding.bleControlVp.offscreenPageLimit = 2
        mViewDataBinding.bleControlVp.adapter = DownLightControlPageAdapter( childFragmentManager)
        mViewDataBinding.handlers = ToolBarEventHandler()
        return mViewDataBinding.root
    }


    override fun updateViewData(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            mViewDataBinding.control = controlDevice
            mControlDevice = controlDevice
        }else{
            mViewDataBinding.control = null
            mControlDevice=null
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