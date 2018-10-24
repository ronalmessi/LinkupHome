package com.ihomey.linkuphome.control

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.bed.BedControlSettingFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.DeviceInfo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.DeviceRenameFragment
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.scan.ScanActivity
import com.ihomey.linkuphome.scene.BedSceneSettingFragment
import com.ihomey.linkuphome.scene.LEDSceneSettingFragment
import com.ihomey.linkuphome.scene.RGBSceneSettingFragment
import com.ihomey.linkuphome.share.ShareActivity
import com.ihomey.linkuphome.time.BedTimerSettingFragment
import com.ihomey.linkuphome.time.RepeatTimerSettingFragment
import com.ihomey.linkuphome.time.TimerSettingFragment
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.RGBCircleView
import com.ihomey.linkuphome.widget.RadioGroupPlus
import com.ihomey.linkuphome.widget.ToggleButtonGroup
import com.ihomey.linkuphome.widget.dashboardview.DashboardView
import com.ihomey.linkuphome.widget.toprightmenu.TopRightMenu


/**
 * Created by dongcaizheng on 2018/4/15.
 */
abstract class BaseControlFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener, RadioGroupPlus.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, ToggleButtonGroup.OnCheckedChangeListener, RGBCircleView.ColorValueListener, DashboardView.ColorTemperatureListener, TopRightMenu.OnMenuItemClickListener, View.OnClickListener {

    protected var lampCategory: Int = -1
    private var controller: Controller? = null
    protected var mControlDevice: ControlDevice? = null
    protected var mViewModel: MainViewModel? = null
    protected lateinit var listener: MeshServiceStateListener
    protected lateinit var mTopRightMenu: TopRightMenu

    abstract fun updateViewData(controlDevice: ControlDevice?)

    private fun initTopRightMenu(context: Context) {
        mTopRightMenu = TopRightMenu(context as Activity)
        mTopRightMenu.setWidth(context.getScreenW() * 5 / 12)
        mTopRightMenu.showIcon(true).dimBackground(false).needAnimationStyle(true).setAnimationStyle(R.style.TRM_ANIM_STYLE)
        mTopRightMenu.setOnMenuItemClickListener(this)
    }

    private fun showTopRightMenu(view: View) {
        val xOffSet = context.getScreenW() * 5 / 12 - context.dip2px(22f) - view.width / 2
        mTopRightMenu.showAsDropDown(view, -xOffSet, context.dip2px(11f))
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as MeshServiceStateListener
        initTopRightMenu(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel?.getCurrentControlDevice()?.observe(this, Observer<Resource<ControlDevice>> {
            if (it?.status == Status.SUCCESS) {
                updateViewData(it.data)
                Log.d("aa", "hahahasfsdf" + "---" + this.javaClass.simpleName)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_$lampCategory", -1)
        mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(lampCategory, lastUsedDeviceId))
    }

    fun initController(type: Int) {
        lampCategory = type
        controller = ControllerFactory().createController(lampCategory)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onColorValueChanged(time: Int) {
        if (mControlDevice != null) {
            if (mControlDevice?.device?.type!! < 5) {
                if (listener.isMeshServiceConnected()) controller?.setLightColor(mControlDevice?.id!!, CODE_LIGHT_COLORS[time])
            } else {
                mControlDevice?.device?.macAddress?.let { controller?.setLightColor(it, CODE_LIGHT_COLORS[time]) }
            }
        }
        mControlDevice?.state?.colorPosition = (time * 151 * (2 * Math.PI) / 3600).toFloat()
        mControlDevice?.state?.changeMode = -1
        mControlDevice?.state?.light = 0
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onColorValueChange(time: Int) {
        if (mControlDevice != null) {
            if (mControlDevice?.device?.type!! < 5) {
                if (listener.isMeshServiceConnected()) controller?.setLightColor(mControlDevice?.id!!, CODE_LIGHT_COLORS[time])
            } else {
                mControlDevice?.device?.macAddress?.let { controller?.setLightColor(it, CODE_LIGHT_COLORS[time]) }
            }
        }
        mControlDevice?.state?.colorPosition = (time * 151 * (2 * Math.PI) / 3600).toFloat()
        mControlDevice?.state?.changeMode = -1
        mControlDevice?.state?.light = 0
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onColorTemperatureValueChanged(temperature: Int) {
        if (mControlDevice != null) {
            if (mControlDevice?.device?.type!! < 5) {
                if (listener.isMeshServiceConnected()) controller?.setLightColorTemperature(mControlDevice?.id!!, temperature)
            } else {
                mControlDevice?.device?.macAddress?.let { controller?.setLightColorTemperature(it, temperature) }
            }
        }
        mControlDevice?.state?.colorTemperature = temperature
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean) {
        if (mControlDevice != null) {
            if (mControlDevice?.device?.type!! < 5) {
                if (listener.isMeshServiceConnected()) controller?.setLightSpeed(mControlDevice?.id!!, position)
            } else {
                mControlDevice?.device?.macAddress?.let { controller?.setLightSpeed(it, position) }
            }
        }
        if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightSpeed(mControlDevice?.id!!, position)
        mControlDevice?.state?.changeMode = position
        mControlDevice?.state?.light = 0
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (mControlDevice != null) {
            if (mControlDevice?.device?.type!! < 5) {
                if (listener.isMeshServiceConnected()) controller?.setLightBright(mControlDevice?.id!!, seekBar.progress.plus(15))
            } else {
                mControlDevice?.device?.macAddress?.let { controller?.setLightBright(it, seekBar.progress.plus(15)) }
            }
        }
        mControlDevice?.state?.brightness = seekBar.progress
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.device_state_cb_power -> {
                if (mControlDevice != null) {
                    mControlDevice?.state?.on = if (isChecked) 1 else 0
                    if (mControlDevice?.device?.type!! < 5) {
                        if (listener.isMeshServiceConnected()) controller?.setLightPowerState(mControlDevice?.id!!, if (isChecked) 1 else 0)
                    } else {
                        mControlDevice?.device?.macAddress?.let { controller?.setLightPowerState(it, if (isChecked) 1 else 0) }
                    }
                }
            }
        }
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onCheckedChanged(group: RadioGroupPlus?, checkedId: Int) {
        var sceneModeValue = -1
        when (checkedId) {
            R.id.rb_scene_read_rgb, R.id.rb_scene_spring_led -> sceneModeValue = 0
            R.id.rb_scene_sunset_rgb, R.id.rb_scene_rainforest_led -> sceneModeValue = 1
            R.id.rb_scene_rest_rgb, R.id.rb_scene_sunset_led -> sceneModeValue = 2
            R.id.rb_scene_spring_rgb, R.id.rb_scene_lighting_led -> sceneModeValue = 3
            R.id.rb_scene_rainforest_rgb -> sceneModeValue = 4
        }
        if (mControlDevice != null) {
            if (mControlDevice?.device?.type!! < 5) {
                if (listener.isMeshServiceConnected()) controller?.setLightScene(mControlDevice?.id!!, sceneModeValue)
            } else {
                mControlDevice?.device?.macAddress?.let { controller?.setLightScene(it, sceneModeValue) }
            }
        }
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onMenuItemClick(position: Int, itemId: Int) {
        if (lampCategory != -1) {
            when (itemId) {
                R.mipmap.toolbar_menu_rename -> {
                    if (mControlDevice != null) {
                        val dialog = DeviceRenameFragment()
                        val bundle = Bundle()
                        bundle.putString("controlDeviceName", mControlDevice?.device?.name)
                        bundle.putInt("controlDeviceId", mControlDevice?.id!!)
                        bundle.putInt("controlDeviceType", mControlDevice?.device?.type!!)
                        dialog.arguments = bundle
                        dialog.show(activity.fragmentManager, "DeviceRenameFragment")
                    }
                }
                R.mipmap.toolbar_menu_alarm -> {
                    val controlDeviceId = mControlDevice?.id ?: -1
                    if (controlDeviceId != -1) {
                        val newFrag = if (lampCategory == 4) RepeatTimerSettingFragment().newInstance() else TimerSettingFragment().newInstance()
                        val fsh = activity as IFragmentStackHolder
                        fsh.replaceFragment(R.id.container, newFrag)
                    }
                }
                R.mipmap.toolbar_menu_scan -> {
                    val intent = Intent(activity, ScanActivity::class.java)
                    intent.putExtra("lampCategoryType", lampCategory)
                    activity.startActivityForResult(intent, REQUEST_CODE_SCAN)
                }
                R.mipmap.toolbar_menu_share -> {
                    val intent = Intent(activity, ShareActivity::class.java)
                    intent.putExtra("lampCategoryType", lampCategory)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.device_color_cb_lighting -> {
                mControlDevice?.state?.light = 1
                mControlDevice?.state?.changeMode = -1
                if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightingMode(mControlDevice?.id!!)
                mViewModel?.updateDevice(mControlDevice)
            }
            R.id.device_state_cb_setting -> {
                val newFrag = BedControlSettingFragment().newInstance()
                val fsh = parentFragment.parentFragment as IFragmentStackHolder
                fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
            }
            R.id.rl_control_setting_scene_mode -> {
                mControlDevice?.device?.macAddress?.let {
                    val newFrag = BedSceneSettingFragment().newInstance()
                    val fsh = parentFragment as IFragmentStackHolder
                    fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
                }
            }
            R.id.rl_control_setting_alarm -> {
                val newFrag = BedTimerSettingFragment().newInstance()
                val fsh = parentFragment as IFragmentStackHolder
                fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
            }
            R.id.rl_control_setting_sync_time -> mControlDevice?.device?.macAddress?.let { syncTime(it) }
        }
    }

    inner class ToolBarEventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> activity.onBackPressed()
                R.id.toolbar_right_setting -> showTopRightMenu(view)
                R.id.device_scene_cb_scene -> {
                    if (parentFragment is IFragmentStackHolder) {
                        val newFrag = if (lampCategory == 1) RGBSceneSettingFragment().newInstance() else LEDSceneSettingFragment().newInstance(mControlDevice?.device?.type
                                ?: -1)
                        val fsh = parentFragment as IFragmentStackHolder
                        fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
                    }
                }
            }
        }
    }

}