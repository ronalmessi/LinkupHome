package com.ihomey.linkuphome.adapter


import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class UnBondedDeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {

    private val selectedDevices = mutableListOf<SingleDevice>()

    fun clearSelectedDeviceIds() {
        selectedDevices.clear()
    }

    fun setSelectedDevices(mSelectedDevices: List<SingleDevice>?) {
        selectedDevices.clear()
        mSelectedDevices?.let { selectedDevices.addAll(mSelectedDevices) }
    }

    fun getSelectedDevices():List<SingleDevice> {
        return selectedDevices
    }

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.name)
        helper.setImageResource(R.id.iv_device_icon, AppConfig.DEVICE_ICON[item.type])
        helper.setChecked(R.id.cb_device_state, selectedDevices.contains(item))
        helper.setOnCheckedChangeListener(R.id.cb_device_state) { _, isChecked -> if (isChecked) selectedDevices.add(item) else selectedDevices.remove(item) }
    }
}