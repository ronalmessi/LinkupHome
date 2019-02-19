package com.ihomey.linkuphome.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SingleDevice


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class UnBondedDeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {

     val selectedDeviceIds = mutableListOf<Int>()

    fun clearSelectedDeviceIds(){
        selectedDeviceIds.clear()
    }

    private val icons = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.device.name)
        helper.setImageResource(R.id.iv_device_icon, icons[item.device.type])
        helper.setOnCheckedChangeListener(R.id.cb_device_state) { _, isChecked -> if (isChecked) selectedDeviceIds.add(item.id) else selectedDeviceIds.remove(item.id) }
    }
}