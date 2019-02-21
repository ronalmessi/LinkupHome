package com.ihomey.linkuphome.adapter


import android.text.TextUtils
import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.entity.Zone


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class Language1ListAdapter(layoutId: Int, data: List<String>) : BaseQuickAdapter<String, BaseViewHolder>(layoutId, data) {

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tv_language_name, item)
        val desLanguage = AppConfig.LANGUAGE[helper.adapterPosition]
        val currentLanguage = LocaleHelper.getLanguage(mContext)
        helper.setGone(R.id.iv_current_language, TextUtils.equals(currentLanguage, desLanguage))
    }
}