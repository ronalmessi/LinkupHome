package com.ihomey.linkuphome.adapter


import androidx.core.content.ContextCompat.getDrawable
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SingleDevice
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.getIcon


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class DeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {

    private val icons = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.device.name)
        helper.setImageResource(R.id.iv_device_icon, icons[item.device.type])
        val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.tv_device_name)
    }
}