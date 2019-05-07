package com.ihomey.linkuphome.adapter

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.RoomAndDevices
import com.suke.widget.SwitchButton


class RoomViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_room_list, parent, false)) {

    val swipeLayout = itemView.findViewById<SwipeLayout>(R.id.swipeLayout)
    val nameView = itemView.findViewById<TextView>(R.id.tv_sub_zone_name)
    val iconView = itemView.findViewById<ImageView>(R.id.iv_sub_zone_type)
    val brightnessLayout = itemView.findViewById<RelativeLayout>(R.id.rl_brightness)
    val brightnessView = itemView.findViewById<SeekBar>(R.id.device_seek_bar_brightness)
    val powerStateView = itemView.findViewById<SwitchButton>(R.id.sb_power)

     val deleteBtn = itemView.findViewById<TextView>(R.id.btn_delete)
     val addBtn = itemView.findViewById<TextView>(R.id.btn_add)
     val lightingBtn = itemView.findViewById<ImageView>(R.id.iv_lighting)
     val colorCyclingBtn = itemView.findViewById<ImageView>(R.id.iv_color_cycling)

    var roomAndDevices: RoomAndDevices? = null


    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(roomAndDevices: RoomAndDevices) {
        this.roomAndDevices = roomAndDevices

        val room = roomAndDevices.room
        room?.let {
            nameView.text = it.name
            val type = it.type.minus(1)
            iconView.setImageResource(AppConfig.ROOM_ICON[type])
            powerStateView.isChecked = it.parameters?.on == 1
            brightnessView.progress = it.parameters?.brightness ?: 20
            if (it.deviceTypes.length == 1&&!TextUtils.equals("3",it.deviceTypes)&&!TextUtils.equals("8",it.deviceTypes)) colorCyclingBtn.visibility = View.VISIBLE else colorCyclingBtn.visibility = View.GONE
        }

        if (roomAndDevices.devices.isNullOrEmpty()) {
            brightnessLayout.visibility = View.GONE
            colorCyclingBtn.visibility = View.GONE
            lightingBtn.visibility = View.GONE
            powerStateView.visibility = View.GONE
            addBtn.visibility = View.VISIBLE
        } else {
            brightnessLayout.visibility = View.VISIBLE
            lightingBtn.visibility = View.VISIBLE
            powerStateView.visibility = View.VISIBLE
            addBtn.visibility = View.GONE
        }
    }

}