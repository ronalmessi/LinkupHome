package com.ihomey.linkuphome.alarm

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.EmtValueListener
import kotlinx.android.synthetic.main.environmental_indicators_fragment.*
import android.text.style.RelativeSizeSpan
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator


open class EnvironmentalIndicatorsFragment : BaseFragment(), EmtValueListener {

    companion object {
        fun newInstance() = EnvironmentalIndicatorsFragment()
    }

    protected lateinit var mViewModel: HomeActivityViewModel

    private lateinit var listener: EnvironmentalIndicatorsListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.environmental_indicators_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as EnvironmentalIndicatorsListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
        fl_refresh.setOnClickListener {
            startAnimation()
        }
        startAnimation()
    }

    override fun onEmtValueChanged(pm25Value:Int,hchoValue: Int,vocValue: Int) {
        stopAnimation()
        val relativeSizeSpan = RelativeSizeSpan(2.2f)
        val hchoSpannableString = SpannableString("$hchoValue ug/m³")
        hchoSpannableString.setSpan(relativeSizeSpan, 0, hchoSpannableString.length-6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_hcho_value.text=hchoSpannableString
        when (hchoValue) {
            in 0..1000 -> {
                tv_hcho_value_level.text = "正常"
            }
            in 1001..2000 -> {
                tv_hcho_value_level.text = "轻度污染"
            }
            in 2001..5000 -> {
                tv_hcho_value_level.text = "重度污染"
            }
            in 5001..10000 -> {
                tv_hcho_value_level.text = "严重污染"
            }
        }

        val pm25SpannableString = SpannableString("$pm25Value ug/m³")
        pm25SpannableString.setSpan(relativeSizeSpan, 0, hchoSpannableString.length-6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_pm25_value.text=pm25SpannableString
        when (pm25Value) {
            in 0..50 -> {
                tv_pm25_value_level.text = "优"
            }
            in 51..100 -> {
                tv_pm25_value_level.text = "良"
            }
            in 101..150 -> {
                tv_pm25_value_level.text = "轻度污染"
            }
            in 151..200 -> {
                tv_pm25_value_level.text = "中度污染"
            }
            in 201..300 -> {
                tv_pm25_value_level.text = "重度污染"
            }
            in 301..500 -> {
                tv_pm25_value_level.text = "严重污染"
            }
        }
        tv_voc_value.text=""+vocValue
        if (vocValue == 0) {
            tv_voc_value_level.text = "洁净空气"
        } else if (vocValue == 1) {
            tv_voc_value_level.text = "轻微污染"
        } else if (vocValue == 2) {
            tv_voc_value_level.text = "中度污染"
        } else if (vocValue == 3) {
            tv_voc_value_level.text = "重度污染"
        }
    }


    private fun startAnimation() {
        listener.getEnvironmentalIndicators(this)
        btn_refresh.isActivated = true
        btn_home.isActivated = true
        val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
        rotate.interpolator = LinearInterpolator()
        btn_refresh.startAnimation(rotate)
    }

    private fun stopAnimation() {
        btn_refresh.isActivated = false
        btn_home.isActivated = false
        btn_refresh.clearAnimation()
    }

    interface EnvironmentalIndicatorsListener {
        fun getEnvironmentalIndicators(listener: EmtValueListener?)
    }

}
