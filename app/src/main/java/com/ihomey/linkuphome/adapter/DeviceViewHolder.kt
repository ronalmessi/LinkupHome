package com.ihomey.linkuphome.adapter

import android.util.Log
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
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.suke.widget.SwitchButton


class DeviceViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_device_list, parent, false)) {

     val containerLayout = itemView.findViewById<ConstraintLayout>(R.id.cl_devices_item)
     val swipeLayout = itemView.findViewById<SwipeLayout>(R.id.swipeLayout)
     val nameView = itemView.findViewById<TextView>(R.id.tv_device_name)
     val iconView = itemView.findViewById<ImageView>(R.id.iv_device_icon)
     val brightnessView = itemView.findViewById<SeekBar>(R.id.device_seek_bar_brightness)
     val powerStateView = itemView.findViewById<SwitchButton>(R.id.sb_power)

    private val deleteBtn = itemView.findViewById<TextView>(R.id.btn_delete)

    var singleDevice: SingleDevice? = null

    var isSwiping:Boolean= false

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(singleDevice: SingleDevice, mOnItemClickListener: DeviceListAdapter.OnItemClickListener?, mOnItemChildClickListener: DeviceListAdapter.OnItemChildClickListener?) {
        this.singleDevice = singleDevice
        swipeLayout.addSwipeListener(object :SwipeLayout.SwipeListener{
            override fun onOpen(layout: SwipeLayout?) {
            }

            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                isSwiping=true
            }

            override fun onStartOpen(layout: SwipeLayout?) {
            }

            override fun onStartClose(layout: SwipeLayout?) {
            }

            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}

            override fun onClose(layout: SwipeLayout?) {
                swipeLayout.postDelayed({ isSwiping=false},550)
            }
        })

        swipeLayout.setOnClickListener {
           if(!isSwiping) mOnItemClickListener?.onItemClick(singleDevice)
        }
        deleteBtn.setOnClickListener {
           mOnItemChildClickListener?.onItemChildClick(singleDevice,it)
        }
        nameView.text = singleDevice.name
        val type= singleDevice.type.minus(1)
        iconView.setImageResource(AppConfig.DEVICE_ICON[type])
        val layoutParams = nameView.layoutParams as ViewGroup.MarginLayoutParams
        if (type == 0) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._21sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._3sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        }else if (type == 1||type==2) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._36sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._16sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        }else if (type==6||type==7) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._36sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._4sdp).toInt())
        } else if (type == 3) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._8sdp).toInt()
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._10sdp).toInt(),parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._10sdp).toInt())
        }else if (type == 4) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._39sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._18sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        }else if (type == 5||type == 9) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._27sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._4sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        }else if (type == 8) {
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt())
        }
        nameView.layoutParams = layoutParams
        powerStateView.isChecked = singleDevice.parameters?.on == 1
        brightnessView.progress = singleDevice.parameters?.brightness?:20
    }

}