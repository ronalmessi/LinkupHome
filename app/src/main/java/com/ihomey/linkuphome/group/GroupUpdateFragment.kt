package com.ihomey.linkuphome.group

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ihomey.linkuphome.R

/**
 * Created by dongcaizheng on 2018/4/14.
 */
class GroupUpdateFragment : DialogFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0f)
        val view = inflater.inflate(R.layout.dialog_group_update, container, false)
        val tipText = view.findViewById<TextView>(R.id.group_update_tv_text)
//        if (arguments?.getInt("updateType", -1) == 0) {
//            tipText.setText(R.string.adding)
//        } else {
//            tipText.setText(R.string.removing)
//        }
        return view
    }

}