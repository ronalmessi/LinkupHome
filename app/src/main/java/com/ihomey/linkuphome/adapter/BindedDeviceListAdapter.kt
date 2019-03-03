package com.ihomey.linkuphome.adapter


import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
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
        val tv_device_name = helper.getView<TextView>(R.id.tv_device_name)
        val layoutParams = tv_device_name.layoutParams as ViewGroup.MarginLayoutParams
        val rl_binded_devices_item=helper.getView<RelativeLayout>(R.id.rl_binded_devices_item)
        if (item.type == 0) {
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt())
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
        } else if (item.type == 1||item.type==2||item.type==6||item.type==7) {
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt())
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._15sdp).toInt()
        }else if (item.type == 4) {
            helper.itemView.setPadding( mContext.resources.getDimension(R.dimen._21sdp).toInt(), 0,mContext.resources.getDimension(R.dimen._12sdp).toInt(), 0)
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._18sdp).toInt()
        }else if (item.type == 5) {
            helper.itemView.setPadding( mContext.resources.getDimension(R.dimen._10sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt(),mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt())
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._4sdp).toInt()
        }else if (item.type == 3) {
            helper.itemView.setPadding(0, mContext.resources.getDimension(R.dimen._6sdp).toInt(),mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._6sdp).toInt())
        }
        tv_device_name.layoutParams=layoutParams
        helper.setText(R.id.tv_device_name, item.name)
        helper.setImageResource(R.id.iv_device_icon, AppConfig.DEVICE_ICON[item.type])
        val sb_power = helper.getView<CheckBox>(R.id.cb_device_power_state)
        sb_power.isChecked = item.state.on == 1
        sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }
    }


    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: SingleDevice, isChecked: Boolean)
    }

}