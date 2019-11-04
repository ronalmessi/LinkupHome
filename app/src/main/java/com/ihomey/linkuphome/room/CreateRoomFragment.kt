package com.ihomey.linkuphome.room

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
import com.ihomey.linkuphome.listener.CreateSubZoneListener


class CreateRoomFragment : DialogFragment() {

    private var createSubZoneListener: CreateSubZoneListener? = null

    fun setCreateSubZoneListener(createSubZoneListener: CreateSubZoneListener) {
        this.createSubZoneListener = createSubZoneListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_create_room, container, false)
        val et_zone_name = view.findViewById<EditText>(R.id.et_zone_name)
        val btn_cancel = view.findViewById<TextView>(R.id.btn_cancel)
        btn_cancel.setOnClickListener { dismiss() }
        val btn_confirm = view.findViewById<TextView>(R.id.btn_confirm)
        btn_confirm.setOnClickListener {
            if (!TextUtils.isEmpty(et_zone_name.text.toString().trim())) {
                arguments?.getInt("zoneTYpe")?.let { it1 -> createSubZoneListener?.createSubZone(it1, et_zone_name.text.toString().trim()) }
            }
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