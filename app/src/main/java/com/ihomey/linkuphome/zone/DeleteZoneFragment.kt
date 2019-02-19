package com.ihomey.linkuphome.zone

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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.ihomey.linkuphome.listener.DeleteSubZoneListener

class DeleteZoneFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.delete_zone_fragment, container, false)
        val tv_dialog_delete_zone_tip = view.findViewById<TextView>(R.id.tv_dialog_delete_zone_tip)
        tv_dialog_delete_zone_tip.text = arguments?.getString("hintText")
        view.findViewById<ImageButton>(R.id.btn_close).setOnClickListener { dismiss() }
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