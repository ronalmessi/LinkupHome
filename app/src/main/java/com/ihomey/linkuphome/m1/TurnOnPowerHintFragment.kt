package com.ihomey.linkuphome.m1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import android.text.Spannable
import android.text.SpannableString
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
        val spannableString = SpannableString("按下背部   开关键开启床头灯，此时\n你将听到欢迎语。")
        val image = CenterImageSpan(context, R.mipmap.ic_power_button)
        spannableString.setSpan(image, 5, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_turn_on_power_hint.text = spannableString
    }

}
