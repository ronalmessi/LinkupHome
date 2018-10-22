package com.ihomey.linkuphome.adapter

import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.SparseIntArray
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.Alarm
import com.ihomey.linkuphome.data.vo.GroupDevice
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.getIcon


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class AlarmSettingAdapter(private val isSingleChoice: Boolean, layoutId: Int, data: MutableList<String>) : BaseQuickAdapter<String, BaseViewHolder>(layoutId, data) {

    val selectedItems: HashMap<Int, Int> = HashMap()

    fun isItemSelected(position: Int): Boolean {
        return selectedItems[position] != null
    }

    fun clearSelectedItems() {
        selectedItems.clear()
    }

    fun setItemSelected(position: Int, selected: Boolean) {
        if (isSingleChoice) selectedItems.clear()
        if (selected) {
            selectedItems[position] = position
        } else {
            selectedItems.remove(position)
        }
        if (!isSingleChoice) notifyItemChanged(position) else notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tv_title, item)
        val titleTextView = helper.getView<TextView>(R.id.tv_title)
        val drawable = ContextCompat.getDrawable(mContext, if (!isItemSelected(helper.adapterPosition)) R.mipmap.state_icon_negative else R.mipmap.state_icon_positive)
        drawable.setBounds(0, 0, titleTextView.lineHeight, titleTextView.lineHeight)
        titleTextView.setCompoundDrawables(null, null, drawable, null)
    }
}

