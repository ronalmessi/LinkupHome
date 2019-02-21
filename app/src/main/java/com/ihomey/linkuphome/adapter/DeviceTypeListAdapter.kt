package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import android.text.Spanned
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.text.SpannableString
import com.ihomey.linkuphome.AppConfig


/**
 * Created by dongcaizheng on 2018/4/9.
 */
class DeviceTypeListAdapter(layoutId: Int) : BaseQuickAdapter<Int, BaseViewHolder>(layoutId) {

    override fun convert(helper: BaseViewHolder?, item: Int) {
        val deviceTypeName = AppConfig.DEVICE_MODEL_NAME[item] + "\n" + mContext.getString( AppConfig.DEVICE_NAME[item])
        val spannableString = SpannableString(deviceTypeName)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        helper?.setText(R.id.tv_device_type_name, spannableString)
        helper?.setImageResource(R.id.iv_device_type, AppConfig.DEVICE_ICON[item])
    }
}