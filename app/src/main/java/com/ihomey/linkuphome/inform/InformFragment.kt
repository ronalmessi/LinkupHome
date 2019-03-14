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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.InformFragmentBinding
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.splash.SplashViewModel
import com.ihomey.linkuphome.toast


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class InformFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var mViewDataBinding: InformFragmentBinding

    private lateinit var splashViewModel: SplashViewModel


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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        splashViewModel= ViewModelProviders.of(this).get(SplashViewModel::class.java)
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
                    scheduleScreen()
                }
            }
        }
    }

    private fun scheduleScreen() {
            context?.getIMEI()?.let { it1 -> splashViewModel.getRemoteCurrentZone(it1).observe(viewLifecycleOwner, Observer<Resource<Zone>> {
                if (it?.status == Status.SUCCESS) {
                    goToHomeActivity()
                }else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }
                    goToHomeActivity()
                }
            })
            }
    }

    private fun goToHomeActivity() {
        splashViewModel.getLocalCurrentZone().observe(viewLifecycleOwner, Observer<Resource<Int>> {
            if (it?.status == Status.SUCCESS) {
                val intent=Intent(activity, HomeActivity::class.java)
                intent.putExtra("currentZoneId",it.data)
                startActivity(intent)
                activity?.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
                activity?.finish()
            }
        })
    }
}