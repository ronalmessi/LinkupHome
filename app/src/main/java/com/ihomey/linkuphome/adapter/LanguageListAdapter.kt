package com.ihomey.linkuphome.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.dip2px


/**
 * Created by dongcaizheng on 2017/12/20.
 */
class LanguageListAdapter(private val items: Array<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<LanguageListAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.layoutParams= androidx.recyclerview.widget.RecyclerView.LayoutParams(androidx.recyclerview.widget.RecyclerView.LayoutParams.MATCH_PARENT, androidx.recyclerview.widget.RecyclerView.LayoutParams.WRAP_CONTENT)
        holder.textView.gravity=Gravity.CENTER
        if(position==0){
            holder.textView.setPadding(0, holder.textView.context.dip2px(8f), 0, holder.textView.context.dip2px(10f))
            holder.textView.setText(R.string.welcome_language_select)
        }else{
            holder.textView.setPadding(0, holder.textView.context.dip2px(10f), 0, holder.textView.context.dip2px(12f))
            holder.textView.text = items[position-1]
            holder.textView.textSize=16f
            holder.itemView.setOnClickListener {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TextView(parent.context))
    }

    override fun getItemCount(): Int = items.size+1

    class ViewHolder(val textView: TextView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(textView)

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}