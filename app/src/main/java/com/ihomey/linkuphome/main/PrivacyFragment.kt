package com.ihomey.linkuphome.main

import android.app.Activity
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentPrivacyBinding
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.CompoundButton
import com.iclass.soocsecretary.util.PreferenceHelper


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class PrivacyFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var mViewDataBinding: FragmentPrivacyBinding

    fun newInstance(): PrivacyFragment {
        return PrivacyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy, container, false)
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
                (activity as IFragmentStackHolder).replaceFragment(R.id.container, PrivacyStatementFragment().newInstance(if (textView.id == R.id.privacy_tv_license) 0 else 1))
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
        var hasAgreed by PreferenceHelper("hasAgreed", false)
        if (mViewDataBinding.privacyCbLicense.isChecked && mViewDataBinding.privacyCbStatement.isChecked) {
            mViewDataBinding.privacyBtnStart.isEnabled = true
            mViewDataBinding.privacyBtnStart.alpha = 1f
            hasAgreed = true
        } else {
            mViewDataBinding.privacyBtnStart.isEnabled = false
            mViewDataBinding.privacyBtnStart.alpha = 0.5f
            hasAgreed = false
        }
    }


    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.privacy_btn_start -> {
                    (activity as IFragmentStackHolder).replaceFragment(R.id.container, WelcomeFragment().newInstance())
                }
            }
        }
    }
}