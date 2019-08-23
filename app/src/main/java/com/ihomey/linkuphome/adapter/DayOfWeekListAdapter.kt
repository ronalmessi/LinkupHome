package com.ihomey.linkuphome.adapter


import android.util.Log
import android.util.SparseIntArray
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import java.lang.StringBuilder


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class DayOfWeekListAdapter(private val isSingleChoice: Boolean, layoutId: Int) : BaseQuickAdapter<Int, BaseViewHolder>(layoutId) {

    init {
        addData(AppConfig.DAY_OF_WEEK)
    }

    val selectedItems: SparseIntArray = SparseIntArray(7)

    fun clearSelectedItems() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun isItemSelected(position: Int): Boolean {
        return selectedItems[position]!=0
    }

    fun setItemSelected(position: Int, selected: Boolean) {
        if (isSingleChoice) selectedItems.clear()
        if (selected) {
            selectedItems.put(position,position+1)
        } else {
            selectedItems.delete(position)
        }
        if (!isSingleChoice) notifyItemChanged(position) else notifyDataSetChanged()
    }



    fun getDayOfWeekValue():Int{
        val dayOfWeekHBinaryStr= StringBuilder("0000000")
        for (i in 0 until selectedItems.size()) {
            val key = selectedItems.keyAt(i)
            dayOfWeekHBinaryStr.replace(key, key + 1,"1")
        }
        return Integer.parseInt(dayOfWeekHBinaryStr.toString(), 2)
    }

    override fun convert(helper: BaseViewHolder, item: Int) {
        helper.setText(R.id.tv_name, item)
        val titleTextView = helper.getView<TextView>(R.id.tv_name)
        val drawable = ContextCompat.getDrawable(mContext, if (!isItemSelected(helper.adapterPosition)) R.mipmap.ic_zone_flag else R.mipmap.ic_zone_flag_current)
        drawable?.setBounds(0, 0, titleTextView.lineHeight, titleTextView.lineHeight)
        titleTextView.setCompoundDrawables(null, null, drawable, null)
    }
}

