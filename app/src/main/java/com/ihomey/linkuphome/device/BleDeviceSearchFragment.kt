package com.ihomey.linkuphome.device

import android.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.ihomey.linkuphome.R

/**
 * Created by dongcaizheng on 2018/4/14.
 */
class BleDeviceSearchFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.setDimAmount(0f)
        return inflater.inflate(R.layout.view_ble_device_searching, container, false)
    }

}