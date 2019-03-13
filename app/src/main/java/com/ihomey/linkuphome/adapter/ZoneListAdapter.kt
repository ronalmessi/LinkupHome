package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Zone


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class ZoneListAdapter(layoutId: Int) : BaseQuickAdapter<Zone, BaseViewHolder>(layoutId) {

    override fun convert(helper: BaseViewHolder, item: Zone) {
        helper.setText(R.id.tv_zone_name, item.name)
        helper.setImageResource(R.id.iv_zone_current_flag, if (item.active==1) R.mipmap.ic_zone_flag_current else R.mipmap.ic_zone_flag)
        helper.addOnClickListener(R.id.iv_zone_rename)
    }
}