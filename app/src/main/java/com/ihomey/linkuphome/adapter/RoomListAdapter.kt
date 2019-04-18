package com.ihomey.linkuphome.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ihomey.linkuphome.data.entity.RoomAndDevices

class RoomListAdapter : PagedListAdapter<RoomAndDevices, RoomViewHolder>(diffCallback) {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemChildClickListener: OnItemChildClickListener? = null
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mOnSeekBarChangeListener: OnSeekBarChangeListener? = null


    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindTo(it, mOnItemClickListener, mOnItemChildClickListener,mOnCheckedChangeListener)
            holder.itemView.setOnClickListener { it1 ->
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder = RoomViewHolder(parent)

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
        private val diffCallback = object : DiffUtil.ItemCallback<RoomAndDevices>() {
            override fun areItemsTheSame(oldItem: RoomAndDevices, newItem: RoomAndDevices): Boolean {
                return (oldItem.room?.id == newItem.room?.id )
            }

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: RoomAndDevices, newItem: RoomAndDevices): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(roomAndDevices: RoomAndDevices)
    }

    interface OnItemChildClickListener {
        fun onItemChildClick(roomAndDevices: RoomAndDevices, view: View)
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