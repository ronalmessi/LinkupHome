package com.ihomey.linkuphome.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.suke.widget.SwitchButton


class UnBondedDeviceViewHolder(val parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_unbonded_device, parent, false)) {

     val containerLayout = itemView.findViewById<RelativeLayout>(R.id.rl_unbonded_devices_item)
     val nameView = itemView.findViewById<TextView>(R.id.tv_device_name)
     val iconView = itemView.findViewById<ImageView>(R.id.iv_device_icon)
     val stateView = itemView.findViewById<CheckBox>(R.id.cb_device_state)

    var singleDevice: SingleDevice? = null



    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(singleDevice: SingleDevice, mOnCheckedChangeListener: UnBondedDeviceListAdapter1.OnCheckedChangeListener?) {
        this.singleDevice = singleDevice
        nameView.text = singleDevice.name
        val type= singleDevice.type.minus(1)
        iconView.setImageResource(AppConfig.DEVICE_ICON[type])
        val layoutParams = nameView.layoutParams as ViewGroup.MarginLayoutParams
        if (type == 0) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._20sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._4sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt())
        }else if (type == 1||type==2) {
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._18sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._2sdp).toInt())
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._35sdp).toInt()
        }else if (type==6||type==7) {
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._15sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._8sdp).toInt())
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._33sdp).toInt()
        }else if (type == 4) {
            containerLayout.setPadding( parent.context.resources.getDimension(R.dimen._21sdp).toInt(), 0,parent.context.resources.getDimension(R.dimen._12sdp).toInt(), 0)
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._38sdp).toInt()
        }else if (type == 5||type == 9) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._26sdp).toInt()
            containerLayout.setPadding(parent.context.resources.getDimension(R.dimen._4sdp).toInt(), 0, parent.context.resources.getDimension(R.dimen._12sdp).toInt(), 0)
        }else if (type == 3) {
            layoutParams.marginStart = parent.context.resources.getDimension(R.dimen._8sdp).toInt()
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._13sdp).toInt(), parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._13sdp).toInt())
        }else if (type == 8) {
            containerLayout.setPadding(0, parent.context.resources.getDimension(R.dimen._12sdp).toInt(), parent.context.resources.getDimension(R.dimen._13sdp).toInt(), parent.context.resources.getDimension(R.dimen._13sdp).toInt())
        }
        nameView.layoutParams = layoutParams

//        helper.setChecked(R.id.cb_device_state, selectedDevices.contains(item))
//        helper.setOnCheckedChangeListener(R.id.cb_device_state) { _, isChecked -> if (isChecked) selectedDevices.add(item) else selectedDevices.remove(item) }
    }

}