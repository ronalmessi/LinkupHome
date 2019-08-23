package com.ihomey.linkuphome.adapter


import android.util.SparseIntArray
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R



/**
 * Created by dongcaizheng on 2018/4/11.
 */

class RingListAdapter(private val isSingleChoice: Boolean, layoutId: Int) : BaseQuickAdapter<Int, BaseViewHolder>(layoutId) {

    init {
        addData(AppConfig.RING_LIST)
    }


    private var selectedPosition=0


    fun isItemSelected(position: Int): Boolean {
        return selectedPosition==position
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }


    fun setItemSelected(position: Int, selected: Boolean) {
        if (selected) selectedPosition=position
        notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder, item: Int) {
        helper.setText(R.id.tv_name, item)
        val titleTextView = helper.getView<TextView>(R.id.tv_name)
        val drawable = ContextCompat.getDrawable(mContext, if (!isItemSelected(helper.adapterPosition)) R.mipmap.ic_zone_flag else R.mipmap.ic_zone_flag_current)
        drawable?.setBounds(0, 0, titleTextView.lineHeight, titleTextView.lineHeight)
        titleTextView.setCompoundDrawables(null, null, drawable, null)
    }
}

