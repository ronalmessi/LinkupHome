package com.ihomey.linkuphome.inform

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.databinding.InformFragmentBinding
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.splash.SplashViewModel


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class InformFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var mViewDataBinding: InformFragmentBinding

    private lateinit var mViewModel: InformViewModel

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
        mViewModel= ViewModelProviders.of(activity!!).get(InformViewModel::class.java)
    }

    private fun styleTextView(textView: TextView) {
        val spannableString = SpannableString(textView.text.toString())
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val bundle=Bundle()
                if (textView.id == R.id.privacy_tv_license){
                    bundle.putString("sourceUrl", AppConfig.USER_AGGREEMENT_URL)
                    bundle.putString("title",getString(R.string.title_user_agreement))
                }else{
                    bundle.putString("sourceUrl",AppConfig.PRIVACY_STATEMENt_URL)
                    bundle.putString("title",getString(R.string.title_private_statement))
                }
                NavHostFragment.findNavController(this@InformFragment).navigate(R.id.action_informFragment_to_webViewFragment,bundle)
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
                    val intent=Intent(activity, HomeActivity::class.java)
                    intent.putExtra("currentZoneId",mViewModel.mCurrentZoneId.value)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
                    activity?.finish()
                }
            }
        }
    }
}