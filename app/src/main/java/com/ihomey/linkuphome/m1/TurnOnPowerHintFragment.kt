package com.ihomey.linkuphome.m1

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.widget.CenterImageSpan
import kotlinx.android.synthetic.main.m1_turn_on_power_hint.*


class TurnOnPowerHintFragment : BaseFragment() {

    fun newInstance(): TurnOnPowerHintFragment {
        return TurnOnPowerHintFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.m1_turn_on_power_hint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spannableString = SpannableString(tv_turn_on_power_hint.text)
        val image = CenterImageSpan(context, R.mipmap.ic_power_button)

        val currentLanguage = LocaleHelper.getLanguage(context)
        if (TextUtils.equals("zh-rCN", currentLanguage) || TextUtils.equals("zh-rTW", currentLanguage)) {
            spannableString.setSpan(image, 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            spannableString.setSpan(image, 16, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        tv_turn_on_power_hint.text = spannableString
    }

}
