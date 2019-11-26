package com.ihomey.linkuphome.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.listener.InputDialogInterface


class InputDialogFragment : DialogFragment() {

    private var listener: InputDialogInterface? = null

    private lateinit var editText:EditText

    fun setInputDialogInterface(listener: InputDialogInterface) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_input, container, false)
        editText = view.findViewById(R.id.editText)

        editText.setText(arguments?.getString("inputText"))
        arguments?.getString("inputText")?.length?.let { editText.setSelection(it) }
        view.findViewById<TextView>(R.id.btn_cancel).setOnClickListener { dismiss() }
        view.findViewById<TextView>(R.id.btn_confirm).setOnClickListener {
            val inputText=editText.text.toString().trim()
            if (!TextUtils.isEmpty(inputText)) {
                listener?.onInput(inputText)
            }
            dismiss()
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onStart() {
        super.onStart()
        editText.requestFocus()
        val displayMetrics = DisplayMetrics()
        dialog?.window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        dialog?.window?.setLayout((displayMetrics.widthPixels - context?.resources?.getDimension(R.dimen._32sdp)!!).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}