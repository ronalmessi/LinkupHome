package com.ihomey.linkuphome.adapter

import android.graphics.BitmapFactory
import android.widget.ImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.getIcon
import com.ihomey.linkuphome.scaleBitmap


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
                if(item.device?.type==8){
                    val srcBitmap = scaleBitmap(BitmapFactory.decodeResource(mContext.resources,getIcon(item.device?.type!!)), 0.6f)
                    helper.setImageBitmap(R.id.lamp_group_iv_icon, srcBitmap)
                    helper.getView<ImageView>(R.id.lamp_group_iv_icon).scaleX=1f
                    helper.getView<ImageView>(R.id.lamp_group_iv_icon).scaleY=1f
                    helper.getView<ImageView>(R.id.lamp_group_iv_icon).setPadding(mContext.dip2px(8f),mContext.dip2px(12f),mContext.dip2px(1f),mContext.dip2px(8f))
                }else{
                    helper.setImageResource(R.id.lamp_group_iv_icon, getIcon(item.device?.type!!))
                }
                helper.setText(R.id.lamp_group_tv_name, item.device?.name)
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