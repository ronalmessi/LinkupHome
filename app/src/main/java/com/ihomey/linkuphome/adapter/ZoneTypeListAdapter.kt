package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.LampCategory

/**
 * Created by dongcaizheng on 2018/4/9.
 */
class ZoneTypeListAdapter(layoutId: Int) : BaseQuickAdapter<Int, BaseViewHolder>(layoutId) {

    init {
        addData(listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14))
    }

    private val icons = arrayListOf(R.mipmap.ic_zone_bed_room, R.mipmap.ic_zone_living_room, R.mipmap.ic_zone_dining_room,R.mipmap.ic_zone_kitchen,  R.mipmap.ic_zone_bathroom,R.mipmap.ic_zone_balcony, R.mipmap.ic_zone_corridor, R.mipmap.ic_zone_entrance, R.mipmap.ic_zone_garage, R.mipmap.ic_zone_garden, R.mipmap.ic_zone_office, R.mipmap.ic_zone_bar_counter, R.mipmap.ic_zone_deck, R.mipmap.ic_zone_tv_wall, R.mipmap.ic_zone_other)

    override fun convert(helper: BaseViewHolder?, item: Int) {
        helper?.setImageResource(R.id.iv_zone_type, icons[item])
    }
}