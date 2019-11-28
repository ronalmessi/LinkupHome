package com.ihomey.linkuphome.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ihomey.linkuphome.data.entity.Zone

class ShareZoneListAdapter : PagedListAdapter<Zone, ShareZoneViewHolder>(diffCallback) {


    private var mOnItemChildClickListener:OnItemChildClickListener? = null

    override fun onBindViewHolder(holder: ShareZoneViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindTo(it)
            holder.shareBtn.setOnClickListener {
                mOnItemChildClickListener?.onItemChildClick(position, it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareZoneViewHolder = ShareZoneViewHolder(parent)

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         * <p>
         * When you add a Cheese with the 'Add' button, the PagedListAdapter uses diffCallback to
         * detect there's only a single item difference from before, so it only needs to animate and
         * rebind a single view.
         *
         * @see android.support.v7.util.DiffUtil
         */
        private val diffCallback = object : DiffUtil.ItemCallback<Zone>() {
            override fun areItemsTheSame(oldItem: Zone, newItem: Zone): Boolean {
                return oldItem.id == newItem.id
            }

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: Zone, newItem: Zone): Boolean {
                return oldItem == newItem
            }
        }
    }


    interface OnItemChildClickListener {
        fun onItemChildClick(position: Int, view: View)
    }


    fun setOnItemChildClickListener(listener: OnItemChildClickListener) {
        mOnItemChildClickListener = listener
    }

}