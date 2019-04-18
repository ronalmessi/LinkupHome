package com.ihomey.linkuphome.adapter

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ihomey.linkuphome.data.entity.SingleDevice

class DeviceListAdapter : PagedListAdapter<SingleDevice, DeviceViewHolder>(diffCallback) {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemChildClickListener: OnItemChildClickListener? = null
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mOnSeekBarChangeListener: OnSeekBarChangeListener? = null


    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        getItem(position)?.let {
            Log.d("aa","yyyy")
            holder.bindTo(it, mOnItemClickListener, mOnItemChildClickListener)
            holder.itemView.setOnClickListener { _ ->
                mOnItemClickListener?.onItemClick(it)
            }
            holder.powerStateView.setOnCheckedChangeListener { _, isChecked ->
                mOnCheckedChangeListener?.onCheckedChanged(position, isChecked)
            }
            holder.brightnessView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    mOnSeekBarChangeListener?.onProgressChanged(position, seekBar.progress)
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder = DeviceViewHolder(parent)

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
        private val diffCallback = object : DiffUtil.ItemCallback<SingleDevice>() {
            override fun areItemsTheSame(oldItem: SingleDevice, newItem: SingleDevice): Boolean {
                return (oldItem.id == newItem.id&&oldItem.parameters?.on==newItem.parameters?.on)
            }

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: SingleDevice, newItem: SingleDevice): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(singleDevice: SingleDevice)
    }

    interface OnItemChildClickListener {
        fun onItemChildClick(singleDevice: SingleDevice, view: View)
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(position: Int, isChecked: Boolean)
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(position: Int, progress: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun setOnItemChildClickListener(listener: OnItemChildClickListener) {
        mOnItemChildClickListener = listener
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.mOnCheckedChangeListener = listener
    }

    fun setOnSeekBarChangeListener(listener:OnSeekBarChangeListener) {
        this.mOnSeekBarChangeListener = listener
    }
}