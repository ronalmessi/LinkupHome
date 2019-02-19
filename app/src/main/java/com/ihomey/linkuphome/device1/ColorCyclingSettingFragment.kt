package com.ihomey.linkuphome.device1

import androidx.fragment.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.ihomey.linkuphome.R
import android.util.DisplayMetrics
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.widget.SingleSelectToggleGroup

class ColorCyclingSettingFragment : DialogFragment() {

    private var controller: Controller? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.color_cyclingg_setting_fragment, container, false)
        controller = arguments?.getInt("type")?.let { ControllerFactory().createController(it) }

        val colorCyclingSetting = view.findViewById<SingleSelectToggleGroup>(R.id.device_cycling_sstg_speed)
        colorCyclingSetting.setOnCheckedChangeListener { position, isChecked ->
            //                if (listener.isMeshServiceConnected())
            arguments?.getInt("zoneId")?.let { controller?.setLightSpeed(it, position) }
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        dialog?.window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        dialog?.window?.setLayout((displayMetrics.widthPixels - context?.resources?.getDimension(R.dimen._32sdp)!!).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}