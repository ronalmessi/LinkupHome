package com.ihomey.linkuphome.adapter


import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class ScanDeviceListAdapter(data: MutableList<SingleDevice>) : BaseMultiItemQuickAdapter<SingleDevice, BaseViewHolder>(data) {


    private lateinit var onCheckedChangeListener: DeviceListAdapter.OnCheckedChangeListener
    private lateinit var onSeekBarChangeListener: DeviceListAdapter.OnSeekBarChangeListener

    fun setOnCheckedChangeListener(listener: DeviceListAdapter.OnCheckedChangeListener) {
        this.onCheckedChangeListener = listener
    }

    fun setOnSeekBarChangeListener(listener: DeviceListAdapter.OnSeekBarChangeListener) {
        this.onSeekBarChangeListener = listener
    }


    init {
        addItemType(-1, R.layout.item_scan_device)
        addItemType(1, R.layout.item_device_list)
    }

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.name)
        val type=item.type-1
        helper.setImageResource(R.id.iv_device_icon, AppConfig.DEVICE_ICON[type])
        when (helper.itemViewType) {
            1 -> {
                val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
                swipeLayout.isLeftSwipeEnabled = false
                swipeLayout.isRightSwipeEnabled = false
                val cl_devices_item = swipeLayout.findViewById<ConstraintLayout>(R.id.cl_devices_item)
                if (type == 0) {
                    cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._3sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                } else if (type == 1||type==2||type==6||type==7) {
                    cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._16sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 4) {
                    cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 5) {
                    cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 8) {
                    cl_devices_item.setPadding(0, mContext.resources.getDimension(R.dimen._12sdp).toInt(), mContext.resources.getDimension(R.dimen._8sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt())
                }

                val sb_power = helper.getView<SwitchButton>(R.id.sb_power)
                sb_power.isChecked = item.parameters?.on == 1
//                sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }

                val seek_bar_brightness = helper.getView<SeekBar>(R.id.device_seek_bar_brightness)
                seek_bar_brightness.progress = item.parameters?.brightness ?: 20

                seek_bar_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
//                        onSeekBarChangeListener.onProgressChanged(item, seekBar.progress)
                    }
                })
            }
            -1 -> {
                if (type == 0) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._3sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                } else if (type == 1||type==2||type==6||type==7) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._16sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 4) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 5) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 8) {
                    helper.itemView.setPadding(0, mContext.resources.getDimension(R.dimen._11sdp).toInt(), mContext.resources.getDimension(R.dimen._8sdp).toInt(), mContext.resources.getDimension(R.dimen._11sdp).toInt())
                }
            }
        }
    }
}