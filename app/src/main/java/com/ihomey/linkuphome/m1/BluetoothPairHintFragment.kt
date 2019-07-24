package com.ihomey.linkuphome.m1

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.ihomey.linkuphome.App
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import kotlinx.android.synthetic.main.instruction_fragment.*
import com.ihomey.linkuphome.adapter.ImageViewAdapter
import com.ihomey.linkuphome.adapter.M1InstructionsPageAdapter
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.widget.CenterImageSpan
import kotlinx.android.synthetic.main.m1_bluetooth_pair_hint.*


class BluetoothPairHintFragment : BaseFragment() {

    fun newInstance(): BluetoothPairHintFragment {
        return BluetoothPairHintFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.m1_bluetooth_pair_hint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spannableString1 = SpannableString("打开手机蓝牙设置，在可用设备列表中选择\n带有耳机   图标的M1床头灯， 成功\n连接后的M1床头灯将显示在已配对的\n设备列表中。")
        val image1 = CenterImageSpan(context, R.mipmap.ic_earphone)
        spannableString1.setSpan(image1, 25, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_bluetooth_pair_hint.text = spannableString1


        val spannableString2 = SpannableString("注意:需要连接\n带有   图标的M1，而非 图标的M1")
        spannableString2.setSpan( StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val image2 = CenterImageSpan(context, R.mipmap.ic_earphone)
        spannableString2.setSpan(image2, 11, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val image3 = CenterImageSpan(context, R.mipmap.ic_bluetooth_button)
        spannableString2.setSpan(image3, 21, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_bluetooth_pair_warning.text = spannableString2
    }

}
