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
import android.widget.TextView
import com.ihomey.linkuphome.listener.DeleteDeviceListener
import com.ihomey.linkuphome.listener.DeleteSubZoneListener

class BondDeviceTipFragment : DialogFragment() {

    private var listener: BondDeviceListener? = null

    fun setDeleteDeviceListener(listener: BondDeviceListener) {
        this.listener = listener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bond_device_tip_fragment, container, false)
        val btn_cancel = view.findViewById<TextView>(R.id.btn_cancel)
        btn_cancel.setOnClickListener { dismiss() }
        val btn_confirm = view.findViewById<TextView>(R.id.btn_confirm)
        btn_confirm.setOnClickListener {
            listener?.confirm()
            dismiss()
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


    interface BondDeviceListener {
        fun confirm()
    }
}