package com.ihomey.linkuphome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device


class BondedDeviceViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_binded_device, parent, false)) {

    val containerLayout = itemView.findViewById<RelativeLayout>(R.id.rl_bonded_devices_item)
    val nameView = itemView.findViewById<TextView>(R.id.tv_device_name)
    val iconView = itemView.findViewById<ImageView>(R.id.iv_device_icon)
    val powerStateView = itemView.findViewById<CheckBox>(R.id.cb_device_power_state)

    var device: Device? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(device: Device) {
        this.device = device
        val type = device.type
        val layoutParams = nameView.layoutParams as ViewGroup.MarginLayoutParams
        if (type == 1) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._20sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._4sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt())
        } else if (type == 2 || type == 3) {
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._18sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt())
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._35sdp).toInt()
        } else if (type == 7 || type == 8) {
            iconView.scaleX = 0.8f
            iconView.scaleY = 0.8f
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._15sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt())
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._33sdp).toInt()
        } else if (type == 0) {
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._21sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._12sdp).toInt(), 0)
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._38sdp).toInt()
        } else if (type == 6 || type == 10) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._24sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._12sdp).toInt(), 0)
        } else if (type == 4) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._8sdp).toInt()
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._13sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._13sdp).toInt())
        } else if (type == 9) {
            iconView.scaleX = 0.8f
            iconView.scaleY = 0.8f
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._14sdp).toInt()
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._16sdp).toInt(), parent.context.resources.getDimension(R.dimen._13sdp).toInt(), parent.context.resources.getDimension(R.dimen._16sdp).toInt())
        }
        nameView.layoutParams = layoutParams
        nameView.text = device.name
        iconView.setImageResource(AppConfig.DEVICE_ICON[type])
        powerStateView.isChecked = device.parameters?.on == 1
    }

}