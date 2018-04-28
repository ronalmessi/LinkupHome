package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.ControlDevice

/**
 * Created by dongcaizheng on 2017/12/26.
 */
class ControlDeviceListAdapter(layoutResId: Int) : BaseQuickAdapter<ControlDevice, BaseViewHolder>(layoutResId) {

    override fun convert(helper: BaseViewHolder?, item: ControlDevice?) {
        helper?.setText(R.id.control_device_tv_name, item?.device?.name)
    }
}