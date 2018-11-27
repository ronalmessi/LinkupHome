package com.ihomey.linkuphome.adapter


import android.support.v4.content.ContextCompat.getDrawable
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.clj.fastble.BleManager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SingleDevice
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.getIcon


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class DeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {
    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.lamp_device_mesh_tv_name, item.device.name)
        helper.setImageResource(R.id.lamp_device_mesh_iv_icon, getIcon(item.device.type))
        val stateTextView = helper.getView<TextView>(R.id.lamp_device_mesh_tv_state)
        if(item.device.type<5){
            stateTextView.setText(if (item.id == 0) R.string.state_disconnected else R.string.state_connected)
            val drawable = getDrawable(mContext, if (item.id == 0) R.mipmap.state_icon_negative else R.mipmap.state_icon_positive)
            drawable.setBounds(0, 0, mContext.dip2px(17.5f), mContext.dip2px(17.5f))
            stateTextView.setCompoundDrawables(null, null, drawable, null)
            helper.itemView.setBackgroundResource(R.drawable.selector_control_unit_item_background)
        }else{
            stateTextView.setText(if (BleManager.getInstance().isConnected(item.device.macAddress)) R.string.state_connected else R.string.state_disconnected)
            val drawable = getDrawable(mContext, if (BleManager.getInstance().isConnected(item.device.macAddress)) R.mipmap.state_icon_positive else R.mipmap.state_icon_negative)
            drawable.setBounds(0, 0, mContext.dip2px(17.5f), mContext.dip2px(17.5f))
            stateTextView.setCompoundDrawables(null, null, drawable, null)
            helper.itemView.setBackgroundResource(R.drawable.selector_ble_device_item_background)
        }
    }

    override fun getDefItemViewType(position: Int): Int {
        val singleDevice=getItem(position)
        if(singleDevice!=null){
            if(singleDevice.device.type==5&&!BleManager.getInstance().isConnected(singleDevice.device.macAddress)) return -1
        }
        return super.getDefItemViewType(position)
    }
}