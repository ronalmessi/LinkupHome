package com.ihomey.linkuphome.m1

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.m1_turn_on_bluetooth_hint.*


class TurnOnBluetoothHintFragment : BaseFragment() {

    fun newInstance(): TurnOnBluetoothHintFragment {
        return TurnOnBluetoothHintFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.m1_turn_on_bluetooth_hint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spannableString = SpannableString(tv_turn_on_bluetooth_hint.text)
        val image = CenterImageSpan(context, R.mipmap.ic_bluetooth_button)
        val currentLanguage = LocaleHelper.getLanguage(context)
        if(TextUtils.equals("zh-rCN",currentLanguage)||TextUtils.equals("zh-rTW",currentLanguage)){
            spannableString.setSpan(image, 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }else{
            spannableString.setSpan(image, 15, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        tv_turn_on_bluetooth_hint.text = spannableString
    }

}
