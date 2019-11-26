package com.ihomey.linkuphome.device1

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.RoomAndDevices
import com.ihomey.linkuphome.devicecontrol.controller.LightControllerFactory
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.MeshServiceStateListener
import com.ihomey.linkuphome.widget.SingleSelectToggleGroup
import com.ihomey.linkuphome.widget.ToggleButtonGroup

class ColorCyclingSettingFragment : DialogFragment(), ToggleButtonGroup.OnCheckedChangeListener {

    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var devices: List<Device>
    private lateinit var meshServiceStateListener: MeshServiceStateListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.color_cyclingg_setting_fragment, container, false)
        val colorCyclingSetting = view.findViewById<SingleSelectToggleGroup>(R.id.device_cycling_sstg_speed)
        colorCyclingSetting.setOnCheckedChangeListener(this)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        meshServiceStateListener = context as MeshServiceStateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.mSelectedRoom.observe(this, Observer<RoomAndDevices> {
            devices = it.devices
        })
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        dialog?.window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        dialog?.window?.setLayout((displayMetrics.widthPixels - context?.resources?.getDimension(R.dimen._32sdp)!!).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean) {
        for (index in devices.indices) {
            val device = devices[index]
            Handler().postDelayed({ LightControllerFactory().createColorController(device)?.setCycleMode(position) }, 100L * index)
        }
    }

}