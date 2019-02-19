package com.ihomey.linkuphome.adapter


import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SingleDevice


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class BindedDeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {

    private lateinit var onCheckedChangeListener: OnCheckedChangeListener

    fun setOnCheckedChangeListener(listener:OnCheckedChangeListener) {
        this.onCheckedChangeListener = listener
    }

    private val icons = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.device.name)
        helper.setImageResource(R.id.iv_device_icon, icons[item.device.type])
        val sb_power = helper.getView<CheckBox>(R.id.cb_device_power_state)
        sb_power.isChecked = item.state?.on == 1
        sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }
    }


    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: SingleDevice, isChecked: Boolean)
    }

}