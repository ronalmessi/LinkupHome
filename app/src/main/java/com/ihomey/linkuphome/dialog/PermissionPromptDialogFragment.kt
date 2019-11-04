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

class PermissionPromptDialogFragment : DialogFragment() {


    private var listener: ConfirmButtonClickListener? = null

    fun setConfirmButtonClickListener(listener: ConfirmButtonClickListener) {
        this.listener = listener
    }


    fun newInstance(title: String, content: String, confirmText: String): PermissionPromptDialogFragment {
        val dialogFragment = PermissionPromptDialogFragment()
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("content", content)
        bundle.putString("confirmText", confirmText)
        dialogFragment.arguments = bundle
        return dialogFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = false
        val view = inflater.inflate(R.layout.dialog_permission_prompt, container, false)
        arguments?.getString("title")?.let { view.findViewById<TextView>(R.id.tv_dialog_title).text = it }
        arguments?.getString("content")?.let { view.findViewById<TextView>(R.id.tv_dialog_content).text = it }
        val btn_confirm = view.findViewById<TextView>(R.id.btn_confirm)
        arguments?.getString("confirmText")?.let { btn_confirm.text = it }
        btn_confirm.setOnClickListener {
            listener?.onConfirm()
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
        fun onConfirm()
    }


}