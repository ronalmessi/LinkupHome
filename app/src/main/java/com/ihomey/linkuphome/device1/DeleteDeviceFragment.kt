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
import com.ihomey.linkuphome.listener.DeleteDeviceListener

class DeleteDeviceFragment : DialogFragment() {


    private var deleteListener: DeleteDeviceListener? = null

    fun setDeleteDeviceListener(deleteSubZoneListener: DeleteDeviceListener) {
        this.deleteListener = deleteSubZoneListener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_delete_device, container, false)
        val btn_cancel = view.findViewById<TextView>(R.id.btn_cancel)
        btn_cancel.setOnClickListener { dismiss() }
        val btn_confirm = view.findViewById<TextView>(R.id.btn_confirm)
        btn_confirm.setOnClickListener {
            arguments?.getString("deviceId")?.let { it1 -> arguments?.getInt("deviceInstructId")?.let { it2 -> deleteListener?.deleteDevice(it1, it2) } }
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
}