package com.ihomey.linkuphome.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ihomey.linkuphome.data.entity.Device

class BondedDeviceListAdapter : PagedListAdapter<Device, BondedDeviceViewHolder>(diffCallback) {

    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null


    override fun onBindViewHolder(holder: BondedDeviceViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindTo(it)
            holder.powerStateView.setOnCheckedChangeListener { _, isChecked -> mOnCheckedChangeListener?.onCheckedChanged(position, isChecked) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BondedDeviceViewHolder = BondedDeviceViewHolder(parent)

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
        private val diffCallback = object : DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
                return (oldItem.id == newItem.id)
            }

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(position: Int, isChecked: Boolean)
    }


    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.mOnCheckedChangeListener = listener
    }

}