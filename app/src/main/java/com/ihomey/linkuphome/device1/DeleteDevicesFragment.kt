package com.ihomey.linkuphome.device1

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ihomey.linkuphome.R

class DeleteDevicesFragment : DialogFragment() {


    private var mListener: ConfirmButtonClickListener? = null

    fun setConfirmButtonClickListener(listener: ConfirmButtonClickListener) {
        this.mListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.delete_devices_fragment, container, false)
        val btn_cancel = view.findViewById<TextView>(R.id.btn_cancel)
        btn_cancel.setOnClickListener { dismiss() }
        val btn_confirm = view.findViewById<TextView>(R.id.btn_confirm)
        btn_confirm.setOnClickListener {
            arguments?.getInt("zoneId")?.let { it1 -> mListener?.confirm(it1) }
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

    interface ConfirmButtonClickListener {
        fun confirm(id: Int)
    }
}