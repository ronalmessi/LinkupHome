package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.LampCategory
import android.text.Spanned
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.text.SpannableString



/**
 * Created by dongcaizheng on 2018/4/9.
 */
class DeviceTypeListAdapter(layoutId: Int) : BaseQuickAdapter<LampCategory, BaseViewHolder>(layoutId) {

    private val models = arrayListOf("M1", "N1", "A2", "R2", "C3", "V1", "S1", "S2")
    private val names = arrayListOf(R.string.lamp_outdoor, R.string.lamp_rgb, R.string.lamp_cct, R.string.lamp_led_strip, R.string.lamp_outdoor, R.string.lamp_s1, R.string.lamp_s2, R.string.lamp_s2)
    private val icons = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)

    override fun convert(helper: BaseViewHolder?, item: LampCategory) {
        val deviceTypeName = models[item.type] + "\n" + mContext.getString(names[item.type])
        val spannableString = SpannableString(deviceTypeName)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        helper?.setText(R.id.tv_device_type_name, spannableString)
        helper?.setImageResource(R.id.iv_device_type, icons[item.type])
    }
}