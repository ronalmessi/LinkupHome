package com.ihomey.linkuphome.adapter


import androidx.core.content.ContextCompat.getDrawable
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class ScanDeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {

    private val icons = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.name)
        helper.setImageResource(R.id.iv_device_icon,icons[item.type])
        val stateTextView = helper.getView<TextView>(R.id.tv_device_state)
        stateTextView.setText(if (item.id == 0) R.string.state_disconnected else R.string.state_connected)
        val drawable = getDrawable(mContext, if (item.id == 0) R.mipmap.ic_device_unconnected else R.mipmap.ic_device_connected)
        stateTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }
}