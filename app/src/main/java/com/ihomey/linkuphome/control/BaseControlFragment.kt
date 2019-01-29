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
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.data.vo.DeviceInfo
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.device.DeviceRenameFragment
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.listeners.MeshServiceStateListener
import com.ihomey.linkuphome.main.LampFragment
import com.ihomey.linkuphome.scan.ScanActivity
import com.ihomey.linkuphome.scene.LEDSceneSettingFragment
import com.ihomey.linkuphome.scene.MiniOutdoorSceneSettingFragment
import com.ihomey.linkuphome.scene.RGBSceneSettingFragment
import com.ihomey.linkuphome.scene.S1SceneSettingFragment
import com.ihomey.linkuphome.share.ShareActivity
import com.ihomey.linkuphome.time.RepeatTimerSettingFragment
import com.ihomey.linkuphome.time.TimerSettingFragment
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.RGBCircleView
import com.ihomey.linkuphome.widget.ToggleButtonGroup
import com.ihomey.linkuphome.widget.dashboardview.DashboardView
import com.ihomey.linkuphome.widget.toprightmenu.TopRightMenu


/**
 * Created by dongcaizheng on 2018/4/15.
 */
abstract class BaseControlFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, ToggleButtonGroup.OnCheckedChangeListener, RGBCircleView.ColorValueListener, DashboardView.ColorTemperatureListener, TopRightMenu.OnMenuItemClickListener, View.OnClickListener {

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
        if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightColor(mControlDevice?.id!!, CODE_LIGHT_COLORS[time])
        mControlDevice?.state?.colorPosition = (time * 151 * (2 * Math.PI) / 3600).toFloat()
        mControlDevice?.state?.changeMode = -1
        mControlDevice?.state?.light = 0
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onColorValueChange(time: Int) {
        if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightColor(mControlDevice?.id!!, CODE_LIGHT_COLORS[time])
        mControlDevice?.state?.colorPosition = (time * 151 * (2 * Math.PI) / 3600).toFloat()
        mControlDevice?.state?.changeMode = -1
        mControlDevice?.state?.light = 0
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onColorTemperatureValueChanged(temperature: Int) {
        if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightColorTemperature(mControlDevice?.id!!, temperature)
        mControlDevice?.state?.colorTemperature = temperature
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean) {
        if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightSpeed(mControlDevice?.id!!, position)
        mControlDevice?.state?.changeMode = position
        mControlDevice?.state?.light = 0
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightBright(mControlDevice?.id!!, seekBar.progress.plus(15))
        mControlDevice?.state?.brightness = seekBar.progress
        mViewModel?.updateDevice(mControlDevice)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.device_state_cb_power -> {
                mControlDevice?.state?.on = if (isChecked) 1 else 0
                if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightPowerState(mControlDevice?.id!!, if (isChecked) 1 else 0)
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
                        val newFrag = if (lampCategory == 5) RepeatTimerSettingFragment().newInstance() else TimerSettingFragment().newInstance()
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
        if (v.id == R.id.device_color_cb_lighting) {
            mControlDevice?.state?.light = 1
            mControlDevice?.state?.changeMode = -1
            if (listener.isMeshServiceConnected() && mControlDevice != null) controller?.setLightingMode(mControlDevice?.id!!)
            mViewModel?.updateDevice(mControlDevice)
        }
    }

    inner class ToolBarEventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> activity.onBackPressed()
                R.id.toolbar_right_setting -> showTopRightMenu(view)
                R.id.device_scene_cb_scene -> {
                    if (parentFragment is IFragmentStackHolder) {
                        val fsh = parentFragment as IFragmentStackHolder
                        when (lampCategory) {
                            1 -> {
                                val newFrag = RGBSceneSettingFragment().newInstance(mControlDevice?.id
                                        ?: -1, mControlDevice?.state?.sceneMode)
                                fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
                            }
                            5 -> {
                                val newFrag = MiniOutdoorSceneSettingFragment().newInstance(mControlDevice?.id
                                        ?: -1, mControlDevice?.state?.sceneMode)
                                fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
                            }
                            6 -> {
                                val newFrag = S1SceneSettingFragment().newInstance(mControlDevice?.id
                                        ?: -1, mControlDevice?.state?.sceneMode)
                                fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
                            }
                            else -> {
                                val newFrag = LEDSceneSettingFragment().newInstance(mControlDevice?.id
                                        ?: -1, mControlDevice?.device?.type
                                        ?: -1, mControlDevice?.state?.sceneMode)
                                fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
                            }
                        }
                    }
                }
            }
        }
    }

}