package com.ihomey.linkuphome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Zone


class ZoneViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_zone_list, parent, false)) {


//    helper.setImageResource(R.id.iv_zone_current_flag, if (item.active == 1) R.mipmap.ic_zone_flag_current else R.mipmap.ic_zone_flag)
//    helper.addOnClickListener(R.id.iv_zone_rename)
//
//    val nameTextView = helper.getView<TextView>(R.id.tv_zone_name)
//    nameTextView.text = item.name
//    if (item.type == 1) {
//        val shareFlagDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_zone_share_flag)
//        shareFlagDrawable?.setBounds(0, 0, shareFlagDrawable.intrinsicWidth * nameTextView.lineHeight / shareFlagDrawable.intrinsicHeight * 5 / 6, nameTextView.lineHeight * 5 / 6)
//        nameTextView.setCompoundDrawables(null, null, shareFlagDrawable, null)
//    }


//    val containerLayout = itemView.findViewById<ConstraintLayout>(R.id.cl_devices_item)
//    val swipeLayout = itemView.findViewById<SwipeLayout>(R.id.swipeLayout)


    private val nameView = itemView.findViewById<TextView>(R.id.tv_zone_name)
    private val currentFlagView = itemView.findViewById<ImageView>(R.id.iv_zone_current_flag)
     val reNameBtn = itemView.findViewById<ImageView>(R.id.iv_zone_rename)




//    val brightnessView = itemView.findViewById<SeekBar>(R.id.device_seek_bar_brightness)
//    val powerStateView = itemView.findViewById<SwitchButton>(R.id.sb_power)
//
//    private val deleteBtn = itemView.findViewById<TextView>(R.id.btn_delete)

    var zone: Zone? = null

    fun bindTo(zone: Zone) {
        this.zone = zone
        currentFlagView.setImageResource(if (zone.active == 1) R.mipmap.ic_zone_flag_current else R.mipmap.ic_zone_flag)
        nameView.text = zone.name
        if (zone.type == 1) {
            val shareFlagDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_zone_share_flag)
            shareFlagDrawable?.setBounds(0, 0, shareFlagDrawable.intrinsicWidth * nameView.lineHeight / shareFlagDrawable.intrinsicHeight * 5 / 6, nameView.lineHeight * 5 / 6)
            nameView.setCompoundDrawables(null, null, shareFlagDrawable, null)
        }
    }

}