package com.ihomey.linkuphome.device

import android.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.lzyzsd.circleprogress.DonutProgress
import com.ihomey.linkuphome.R

/**
 * Created by dongcaizheng on 2018/4/14.
 */
class DeviceAssociateFragment : DialogFragment() {

    private lateinit var mProgressView: DonutProgress
    private lateinit var mTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.setDimAmount(0f)
        val view = inflater.inflate(R.layout.fragment_dialog_device_associate, container, false)
        mProgressView = view.findViewById(R.id.device_associate_progress)
        mTextView = view.findViewById(R.id.device_associate_tv_text)
        return view
    }

    fun onAssociateProgressChanged(progress: Int) {
        mProgressView.progress = progress.toFloat()
        mTextView.text = getString(R.string.associating) + progress + "%"
    }
}