package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.GroupDevice



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
//                helper.setImageResource(R.id.lamp_group_iv_icon, getIcon(item.device?.type!!))
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