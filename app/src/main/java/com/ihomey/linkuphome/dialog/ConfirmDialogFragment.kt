package com.ihomey.linkuphome.dialog

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
import com.ihomey.linkuphome.listener.ConfirmDialogInterface

class ConfirmDialogFragment : DialogFragment() {

    private var listener: ConfirmDialogInterface? = null

    fun setConfirmDialogInterface(listener: ConfirmDialogInterface) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_confirm, container, false)
        view.findViewById<TextView>(R.id.tv_dialog_title).text = arguments?.getString("title")
        view.findViewById<TextView>(R.id.tv_dialog_content).text = arguments?.getString("content")
        view.findViewById<TextView>(R.id.btn_cancel).setOnClickListener { dismiss() }
        view.findViewById<TextView>(R.id.btn_confirm).setOnClickListener {
            listener?.onConfirmButtonClick()
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