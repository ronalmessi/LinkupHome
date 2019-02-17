package com.ihomey.linkuphome.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SubZone
import com.daimajia.swipe.SwipeLayout


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class SubZoneListAdapter(layoutId: Int) : BaseQuickAdapter<SubZone, BaseViewHolder>(layoutId) {
    override fun convert(helper: BaseViewHolder, item: SubZone) {
        helper.setText(R.id.tv_sub_zone_name, item.name)
        val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.iv_color_cycling)
        helper.addOnClickListener(R.id.iv_lighting)
    }
}