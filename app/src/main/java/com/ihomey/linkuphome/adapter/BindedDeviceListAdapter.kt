package com.ihomey.linkuphome.adapter


import android.widget.CheckBox
import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class BindedDeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {

    private lateinit var onCheckedChangeListener: OnCheckedChangeListener

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = listener
    }

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.name)

        val rl_binded_devices_item=helper.getView<RelativeLayout>(R.id.rl_binded_devices_item)
        if (item.type == 4) {
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt())
        } else if (item.type == 3) {
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt())
        }
        helper.setImageResource(R.id.iv_device_icon, AppConfig.DEVICE_ICON[item.type])
        val sb_power = helper.getView<CheckBox>(R.id.cb_device_power_state)
        sb_power.isChecked = item.state.on == 1
        sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }
    }


    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: SingleDevice, isChecked: Boolean)
    }

}