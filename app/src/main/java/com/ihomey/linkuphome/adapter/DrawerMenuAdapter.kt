package com.ihomey.linkuphome.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.widget.toprightmenu.MenuItem


/**
 * Created by dongcaizheng on 2017/12/26.
 */
class DrawerMenuAdapter(layoutResId: Int, data: MutableList<MenuItem>?) : BaseQuickAdapter<MenuItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder?, item: MenuItem) {
        helper?.setText(R.id.text_menu_title, item.textRes)
        helper?.setImageResource(R.id.image_menu_icon, item.normalIcon)
    }
}

