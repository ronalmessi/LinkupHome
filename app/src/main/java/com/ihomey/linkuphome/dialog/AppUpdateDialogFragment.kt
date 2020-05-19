package com.ihomey.linkuphome.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.listener.AppUpdateDialogInterface
import com.ihomey.linkuphome.listener.ConfirmDialogInterface

class AppUpdateDialogFragment : DialogFragment() {

    private var listener: AppUpdateDialogInterface? = null

    fun setAppUpdateDialogInterface(listener: AppUpdateDialogInterface) {
        this.listener = listener
    }

    fun newInstance( content: String, isNeedUpdate: Boolean): AppUpdateDialogFragment {
        val dialogFragment = AppUpdateDialogFragment()
        val bundle = Bundle()
        bundle.putString("content", content)
        bundle.putBoolean("isNeedUpdate", isNeedUpdate)
        dialogFragment.arguments = bundle
        return dialogFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_app_update, container, false)
        view.findViewById<TextView>(R.id.tv_dialog_update_content).text = arguments?.getString("content")
        view.findViewById<TextView>(R.id.tv_dialog_update_content).movementMethod = ScrollingMovementMethod.getInstance()
        arguments?.getBoolean("isNeedUpdate",false)?.let {
            view.findViewById<ImageButton>(R.id.btn_cancel).visibility=if(it) View.GONE else View.VISIBLE
        }
        view.findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener { dismiss() }
        view.findViewById<TextView>(R.id.btn_confirm).setOnClickListener {
            listener?.onUpdate()
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