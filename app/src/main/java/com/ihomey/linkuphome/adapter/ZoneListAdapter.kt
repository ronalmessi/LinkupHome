package com.ihomey.linkuphome.adapter

import android.widget.TextView
import androidx.core.content.ContextCompat
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
        helper.setImageResource(R.id.iv_zone_current_flag, if (item.active==1) R.mipmap.ic_zone_flag_current else R.mipmap.ic_zone_flag)
        helper.addOnClickListener(R.id.iv_zone_rename)

        val nameTextView= helper.getView<TextView>(R.id.tv_zone_name)
        nameTextView.text = item.name
        if(item.type==1){
            val shareFlagDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_zone_share_flag)
            shareFlagDrawable?.setBounds(0, 0, shareFlagDrawable.intrinsicWidth*nameTextView.lineHeight/shareFlagDrawable.intrinsicHeight*5/6, nameTextView.lineHeight*5/6)
            nameTextView.setCompoundDrawables(null,null,shareFlagDrawable,null)
        }
    }


    override fun getItemViewType(position: Int): Int {
        val zone=getItem(position)
        zone?.let {
           return it.type
        }
        return 0
    }
}