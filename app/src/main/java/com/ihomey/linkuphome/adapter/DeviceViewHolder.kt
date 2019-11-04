package com.ihomey.linkuphome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.suke.widget.SwitchButton


class DeviceViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device_list, parent, false)) {

    val containerLayout = itemView.findViewById<ConstraintLayout>(R.id.cl_devices_item)
    val swipeLayout = itemView.findViewById<SwipeLayout>(R.id.swipeLayout)
    val nameView = itemView.findViewById<TextView>(R.id.tv_device_name)
    val iconView = itemView.findViewById<ImageView>(R.id.iv_device_icon)
    val brightnessView = itemView.findViewById<SeekBar>(R.id.device_seek_bar_brightness)
    val powerStateView = itemView.findViewById<SwitchButton>(R.id.sb_power)

    private val deleteBtn = itemView.findViewById<TextView>(R.id.btn_delete)

    var device: Device? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(device: Device, mOnItemChildClickListener: DeviceListAdapter.OnItemChildClickListener?) {
        this.device = device
        deleteBtn.setOnClickListener {
            swipeLayout.close()
            mOnItemChildClickListener?.onItemChildClick(device, it)
        }
        val type = device.type
        nameView.text = if (type == 6) "V1" else device.name
        iconView.setImageResource(AppConfig.DEVICE_ICON[type])
        val layoutParams = nameView.layoutParams as ViewGroup.MarginLayoutParams
        if (type == 1) {
            brightnessView.max = 85
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._15sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._3sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        } else if (type == 2 || type == 3) {
            iconView.scaleX = 0.7f
            iconView.scaleY = 0.7f
            if (type == 2) brightnessView.max = 85 else brightnessView.max = 240
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._30sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._16sdp).toInt(), parent.context.resources.getDimension(R.dimen._4sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._4sdp).toInt())
        } else if (type == 7 || type == 8) {
            if (type == 7) brightnessView.max = 85 else brightnessView.max = 240
            iconView.scaleX = 0.8f
            iconView.scaleY = 0.8f
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._30sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._14sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._4sdp).toInt())
        } else if (type == 4) {
            brightnessView.max = 85
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._2sdp).toInt()
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._10sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._10sdp).toInt())
        } else if (type == 0) {
            brightnessView.max = 85
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._32sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._18sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        } else if (type == 6 || type == 10) {
            brightnessView.max = 22
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._21sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._4sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        } else if (type == 9) {
            brightnessView.max = 85
            iconView.scaleX = 0.8f
            iconView.scaleY = 0.8f
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._10sdp).toInt()
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt())
        }
        nameView.layoutParams = layoutParams
        powerStateView.isChecked = device.parameters?.on == 1
        brightnessView.progress = device.parameters?.brightness ?: 20
    }

}