package com.ihomey.linkuphome.adapter

import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.getIcon


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class GroupListAdapter(val lampCategoryType: Int, data: MutableList<GroupDevice>?) : BaseMultiItemQuickAdapter<GroupDevice, BaseViewHolder>(data) {

    init {
        addItemType(1, R.layout.lamp_group_list_item)
        addItemType(-1, R.layout.lamp_group_add_item)
    }

    override fun convert(helper: BaseViewHolder, item: GroupDevice) {
        when (helper.itemViewType) {
            1 -> {
                helper.setText(R.id.lamp_group_tv_name, item.device?.name)
                val drawable = ContextCompat.getDrawable(mContext, getIcon(item.device?.type!!))
                val tintDrawable = DrawableCompat.wrap(drawable).mutate()
                DrawableCompat.setTint(tintDrawable, ContextCompat.getColor(mContext, R.color.lamp_group_tint_color))
                helper.setImageDrawable(R.id.lamp_group_iv_icon, tintDrawable)
                var isShare by PreferenceHelper("share_$lampCategoryType", false)
                if (isShare) {
                    helper.setGone(R.id.lamp_group_tv_setting, false)
                } else {
                    helper.setGone(R.id.lamp_group_tv_setting, true)
                    helper.addOnClickListener(R.id.lamp_group_tv_setting)
                }
            }
        }
    }

}