package com.ihomey.linkuphome.inform

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.databinding.InformFragmentBinding


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class InformFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var mViewDataBinding: InformFragmentBinding

    fun newInstance(): InformFragment {
        return InformFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.inform_fragment, container, false)
        mViewDataBinding.handlers = EventHandler()
        mViewDataBinding.privacyCbLicense.setOnCheckedChangeListener(this)
        mViewDataBinding.privacyCbStatement.setOnCheckedChangeListener(this)
        styleTextView(mViewDataBinding.privacyTvLicense)
        styleTextView(mViewDataBinding.privacyTvStatement)
        return mViewDataBinding.root
    }


    private fun styleTextView(textView: TextView) {
        val spannableString = SpannableString(textView.text.toString())
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                NavHostFragment.findNavController(this@InformFragment).navigate(if (textView.id != R.id.privacy_tv_license) R.id.action_informFragment_to_privacyAgreementFragment else R.id.action_informFragment_to_termsOfUseFragment)
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = Color.BLACK
                textPaint.isUnderlineText = true
            }
        }, 0, textView.text.toString().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        textView.highlightColor = Color.TRANSPARENT
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (mViewDataBinding.privacyCbLicense.isChecked && mViewDataBinding.privacyCbStatement.isChecked) {
            mViewDataBinding.privacyBtnStart.isEnabled = true
            mViewDataBinding.privacyBtnStart.alpha = 1f
        } else {
            mViewDataBinding.privacyBtnStart.isEnabled = false
            mViewDataBinding.privacyBtnStart.alpha = 0.5f
        }
    }

    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.privacy_btn_start -> {
                    var hasAgreed by PreferenceHelper("hasAgreed", false)
                    hasAgreed = true
                    NavHostFragment.findNavController(this@InformFragment).navigate(R.id.action_informFragment_to_homeActivity)
                    activity?.finish()
                }
            }
        }
    }
}