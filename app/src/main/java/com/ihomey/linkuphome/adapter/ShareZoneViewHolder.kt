package com.ihomey.linkuphome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Zone


class ShareZoneViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_zone_share_list, parent, false)) {

    private val nameView = itemView.findViewById<TextView>(R.id.tv_zone_name)
    private val currentFlagView = itemView.findViewById<ImageView>(R.id.iv_zone_current_flag)
    val shareBtn = itemView.findViewById<ImageView>(R.id.iv_zone_share)

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