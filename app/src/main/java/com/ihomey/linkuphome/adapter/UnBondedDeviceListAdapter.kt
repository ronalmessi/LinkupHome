package com.ihomey.linkuphome.adapter


import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class UnBondedDeviceListAdapter(layoutId: Int) : BaseQuickAdapter<Device, BaseViewHolder>(layoutId) {

    private val selectedDevices = mutableListOf<Device>()

    fun getSelectedDevices():List<Device> {
        return selectedDevices
    }

    override fun convert(helper: BaseViewHolder, item: Device) {
        val type=item.type-1
        val tv_device_name = helper.getView<TextView>(R.id.tv_device_name)
        val layoutParams = tv_device_name.layoutParams as ViewGroup.MarginLayoutParams
        val rl_binded_devices_item=helper.getView<RelativeLayout>(R.id.rl_unbonded_devices_item)
        if (type == 0) {
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._20sdp).toInt()
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt())
        }else if (type == 1||type==2) {
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._2sdp).toInt())
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._35sdp).toInt()
        }else if (type==6||type==7) {
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._15sdp).toInt(), mContext.resources.getDimension(R.dimen._8sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._8sdp).toInt())
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._33sdp).toInt()
        }else if (type == 4) {
            helper.itemView.setPadding( mContext.resources.getDimension(R.dimen._21sdp).toInt(), 0,mContext.resources.getDimension(R.dimen._12sdp).toInt(), 0)
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._38sdp).toInt()
        }else if (type == 5||type == 9) {
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._26sdp).toInt()
            rl_binded_devices_item.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._12sdp).toInt(), 0)
        }else if (type == 3) {
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._8sdp).toInt()
            rl_binded_devices_item.setPadding(0, mContext.resources.getDimension(R.dimen._13sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._13sdp).toInt())
        }else if (type == 8) {
            rl_binded_devices_item.setPadding(0, mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._13sdp).toInt(), mContext.resources.getDimension(R.dimen._13sdp).toInt())
        }

        tv_device_name.layoutParams=layoutParams
        helper.setText(R.id.tv_device_name, item.name)
        helper.setImageResource(R.id.iv_device_icon, AppConfig.DEVICE_ICON[type])
        helper.setChecked(R.id.cb_device_state, selectedDevices.contains(item))
        helper.setOnCheckedChangeListener(R.id.cb_device_state) { _, isChecked -> if (isChecked) selectedDevices.add(item) else selectedDevices.remove(item) }
    }
}