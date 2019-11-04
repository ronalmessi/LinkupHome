package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R

/**
 * Created by dongcaizheng on 2018/4/9.
 */
class RoomTypeListAdapter(layoutId: Int) : BaseQuickAdapter<Int, BaseViewHolder>(layoutId) {

    init {
        addData(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14))
    }

    override fun convert(helper: BaseViewHolder?, item: Int) {
        helper?.setImageResource(R.id.iv_zone_type, AppConfig.ROOM_ICON[item])
    }
}