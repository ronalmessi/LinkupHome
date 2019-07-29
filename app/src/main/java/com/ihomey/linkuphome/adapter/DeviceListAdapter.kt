package com.ihomey.linkuphome.adapter

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.data.entity.Device

class DeviceListAdapter : PagedListAdapter<Device, DeviceViewHolder>(diffCallback) {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemChildClickListener: OnItemChildClickListener? = null
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mOnSeekBarChangeListener: OnSeekBarChangeListener? = null

    var isSwiping:Boolean= false

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindTo(it, mOnItemChildClickListener)
            holder.itemView.setOnClickListener {it0->
                mOnItemClickListener?.onItemClick(it)
            }
            holder.swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener{
                override fun onOpen(layout: SwipeLayout?) {
                }

                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                    isSwiping=true
                }

                override fun onStartOpen(layout: SwipeLayout?) {
                }

                override fun onStartClose(layout: SwipeLayout?) {
                }

                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}

                override fun onClose(layout: SwipeLayout?) {
                    holder.swipeLayout.postDelayed({ isSwiping=false},550)
                }
            })
            holder.swipeLayout.setOnClickListener {it0->
                if(!isSwiping) mOnItemClickListener?.onItemClick(it)
            }
            holder.powerStateView.setOnCheckedChangeListener { _, isChecked ->
                mOnCheckedChangeListener?.onCheckedChanged(it, isChecked)
            }
            holder.brightnessView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    mOnSeekBarChangeListener?.onProgressChanged(it, seekBar.progress)
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
        private val diffCallback = object : DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
                return (TextUtils.equals(oldItem.id,newItem.id)&&oldItem.parameters?.on==newItem.parameters?.on&&oldItem.parameters?.brightness==newItem.parameters?.brightness)
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

    interface OnItemClickListener {
        fun onItemClick(singleDevice: Device)
    }

    interface OnItemChildClickListener {
        fun onItemChildClick(singleDevice: Device, view: View)
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(singleDevice: Device, isChecked: Boolean)
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(singleDevice: Device, progress: Int)
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