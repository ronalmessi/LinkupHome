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
        val spannableString1 = SpannableString(tv_bluetooth_pair_hint.text)
        val image1 = CenterImageSpan(context, R.mipmap.ic_earphone)
        val spannableString2 = SpannableString(tv_bluetooth_pair_warning.text)

        val currentLanguage = LocaleHelper.getLanguage(context)
        if(TextUtils.equals("zh-rCN",currentLanguage)||TextUtils.equals("zh-rTW",currentLanguage)){
            iv_phone.setImageResource(R.mipmap.ic_phone_zh)
            spannableString1.setSpan(image1, 23, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannableString2.setSpan( StyleSpan(Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val image2 = CenterImageSpan(context, R.mipmap.ic_earphone)
            spannableString2.setSpan(image2, 10, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val image3 = CenterImageSpan(context, R.mipmap.ic_bluetooth_button)
            spannableString2.setSpan(image3, 21, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }else{
            iv_phone.setImageResource(R.mipmap.ic_phone_en)
            spannableString1.setSpan(image1, 82, 83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString2.setSpan( StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val image2 = CenterImageSpan(context, R.mipmap.ic_earphone)
            spannableString2.setSpan(image2, 34, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val image3 = CenterImageSpan(context, R.mipmap.ic_bluetooth_button)
            spannableString2.setSpan(image3, 57, 58, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        tv_bluetooth_pair_hint.text = spannableString1
        tv_bluetooth_pair_warning.text = spannableString2
    }

}
