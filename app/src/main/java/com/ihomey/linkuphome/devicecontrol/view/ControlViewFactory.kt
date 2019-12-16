package com.ihomey.linkuphome.devicecontrol.view

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.devicecontrol.scene.BaseSceneControlView
import com.ihomey.linkuphome.devicecontrol.scene.N1SceneControlView
import com.ihomey.linkuphome.devicecontrol.scene.R2SceneControlView
import com.ihomey.linkuphome.devicecontrol.scene.V1SceneControlView
import com.ihomey.linkuphome.devicecontrol.switchtimer.BaseSwitchTimerControlView
import com.ihomey.linkuphome.devicecontrol.switchtimer.M1SwitchTimerControlView
import com.ihomey.linkuphome.devicecontrol.switchtimer.RepeatSwitchTimerControlView
import com.ihomey.linkuphome.devicecontrol.switchtimer.SwitchTimerControlView

class ControlViewFactory {

    fun createControlView(deviceType: Int, context: Context, fragment: Fragment): BaseControlView? {
        return when (deviceType) {
            1 -> C3ControlView(R.layout.control_c3_view, context)
            2 -> R2ControlView(R.layout.control_v1_view, context)
            3 -> A2ControlView(R.layout.control_a2_view, context)
            4 -> R2ControlView(R.layout.control_v1_view, context)
            6 -> V1ControlView(R.layout.control_v1_view, context)
            0 -> M1ControlView(R.layout.control_m1_view, context, fragment)
            7 -> R2ControlView(R.layout.control_v1_view, context)
            8 -> A2ControlView(R.layout.control_a2_view, context)
            9 -> T1ControlView(R.layout.control_t1_view, context, fragment)
            10 -> V1ControlView(R.layout.control_v1_view, context)
            else -> null
        }
    }

    fun createSceneControlView(deviceType: Int, context: Context): BaseSceneControlView? {
        return when (deviceType) {
              0,2,7,9 -> R2SceneControlView(R.layout.control_scene_r2_view, context)
              4 -> N1SceneControlView(R.layout.control_scene_n1_view, context)
              6,10 -> V1SceneControlView(R.layout.control_scene_v1_view, context)
            else -> null
        }
    }

    fun createSwitchTimerControlView(parentView: View, device: Device): BaseSwitchTimerControlView? {
        return when (device.type) {
            2,3,4,7,8,9 -> SwitchTimerControlView(parentView,device)
            6 -> RepeatSwitchTimerControlView(parentView,device)
            0 -> M1SwitchTimerControlView(parentView,device)
            else -> null
        }
    }
}