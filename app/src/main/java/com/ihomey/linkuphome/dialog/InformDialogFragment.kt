package com.ihomey.linkuphome.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.WebViewActivity
import com.ihomey.linkuphome.home.HomeActivity
import com.ihomey.linkuphome.listener.InformDialogInterface

class InformDialogFragment : DialogFragment() {

    private var listener: InformDialogInterface? = null

    fun setInformDialogInterface(listener: InformDialogInterface) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_inform, container, false)
        view.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            listener?.onDisAgree()
            dismiss()
        }
        view.findViewById<TextView>(R.id.btn_confirm).setOnClickListener {
            listener?.onAgree()
            dismiss()
        }
        styleTextView(view.findViewById<TextView>(R.id.tv_dialog_content))
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    private fun styleTextView(textView: TextView) {
        val spannableString = SpannableString(textView.text.toString())
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("sourceUrl", AppConfig.USER_AGGREEMENT_BASE_URL + "zh")
                intent.putExtra("title", getString(R.string.title_user_agreement))
                startActivity(intent)
//                val bundle = Bundle()
//                if (textView.id == R.id.privacy_tv_license) {
//                    bundle.putString("sourceUrl", AppConfig.USER_AGGREEMENT_BASE_URL + currentLanguage)
//                    bundle.putString("title", getString(R.string.title_user_agreement))
//                } else {
//                    bundle.putString("sourceUrl", AppConfig.PRIVACY_STATEMENt_BASE_URL + currentLanguage)
//                    bundle.putString("title", getString(R.string.title_private_statement))
//                }
//                NavHostFragment.findNavController(this@InformFragment).navigate(R.id.action_informFragment_to_webViewFragment, bundle)
                Log.d("aa","1111")
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = Color.RED
                textPaint.isUnderlineText = false
            }
        }, 77, 83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("sourceUrl", AppConfig.PRIVACY_STATEMENt_BASE_URL + "cn")
                intent.putExtra("title", getString(R.string.title_private_statement))
                startActivity(intent)
                Log.d("aa","2222")
//                val bundle = Bundle()
//                if (textView.id == R.id.privacy_tv_license) {
//                    bundle.putString("sourceUrl", AppConfig.USER_AGGREEMENT_BASE_URL + currentLanguage)
//                    bundle.putString("title", getString(R.string.title_user_agreement))
//                } else {
//                    bundle.putString("sourceUrl", AppConfig.PRIVACY_STATEMENt_BASE_URL + currentLanguage)
//                    bundle.putString("title", getString(R.string.title_private_statement))
//                }
//                NavHostFragment.findNavController(this@InformFragment).navigate(R.id.action_informFragment_to_webViewFragment, bundle)
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = Color.RED
                textPaint.isUnderlineText = false
            }
        }, 84, 90, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        textView.highlightColor = Color.TRANSPARENT
        textView.movementMethod = LinkMovementMethod.getInstance()
    }



    override fun onStart() {
        super.onStart()
        val displayMetrics = DisplayMetrics()
        dialog?.window?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        dialog?.window?.setLayout((displayMetrics.widthPixels - context?.resources?.getDimension(R.dimen._32sdp)!!).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}