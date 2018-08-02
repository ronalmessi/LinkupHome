package com.ihomey.linkuphome.adapter

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.SingleDevice

/**
 * Created by dongcaizheng on 2017/12/26.
 */
class BondedDeviceAdapter(layoutResId: Int,data:List<SingleDevice>) : BaseItemDraggableAdapter<SingleDevice, BaseViewHolder>(layoutResId,data) {

    override fun convert(helper: BaseViewHolder?, item: SingleDevice?) {
        helper?.setText(R.id.group_setting_tv_device_name, item?.device?.name + "(" + mContext.getResources().getString(R.string.added) + ")")
    }

}