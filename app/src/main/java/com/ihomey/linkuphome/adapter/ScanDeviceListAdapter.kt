package com.ihomey.linkuphome.adapter


import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Device
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class ScanDeviceListAdapter(data: MutableList<Device>) : BaseMultiItemQuickAdapter<Device, BaseViewHolder>(data) {


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

    override fun convert(helper: BaseViewHolder, item: Device) {
        val type=item.type
        val nameView = helper.getView<TextView>(R.id.tv_device_name)
        val iconView = helper.getView<ImageView>(R.id.iv_device_icon)
        nameView.text = if(type==6) "V1" else item.name
        iconView.setImageResource(AppConfig.DEVICE_ICON[type])
        when (helper.itemViewType) {
            1 -> {
                val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
                swipeLayout.isLeftSwipeEnabled = false
                swipeLayout.isRightSwipeEnabled = false
                val layoutParams = nameView.layoutParams as ViewGroup.MarginLayoutParams
                val brightnessView = helper.getView<SeekBar>(R.id.device_seek_bar_brightness)
                brightnessView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        onSeekBarChangeListener.onProgressChanged(item, seekBar.progress)
                    }
                })
                val containerLayout = swipeLayout.findViewById<ConstraintLayout>(R.id.cl_devices_item)
                val powerStateView = helper.getView<SwitchButton>(R.id.sb_power)
                powerStateView.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }
                if (type == 1) {
                    brightnessView.max=85
                    layoutParams.marginStart = mContext.resources.getDimension(R.dimen._15sdp).toInt()
                    containerLayout.setPadding(mContext.resources.getDimension(R.dimen._3sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 2||type==3) {
                    iconView.scaleX=0.7f
                    iconView.scaleY=0.7f
                    if(type == 2) brightnessView.max=85 else brightnessView.max=240
                    layoutParams.marginStart =mContext.resources.getDimension(R.dimen._30sdp).toInt()
                    containerLayout.setPadding(mContext.resources.getDimension(R.dimen._16sdp).toInt(), mContext.resources.getDimension(R.dimen._4sdp).toInt(), mContext.resources.getDimension(R.dimen._8sdp).toInt(), mContext.resources.getDimension(R.dimen._4sdp).toInt())
                }else if (type==7||type==8) {
                    if(type == 7) brightnessView.max=85 else brightnessView.max=240
                    iconView.scaleX=0.8f
                    iconView.scaleY=0.8f
                    layoutParams.marginStart = mContext.resources.getDimension(R.dimen._30sdp).toInt()
                    containerLayout.setPadding(mContext.resources.getDimension(R.dimen._14sdp).toInt(), mContext.resources.getDimension(R.dimen._8sdp).toInt(),mContext.resources.getDimension(R.dimen._8sdp).toInt(),mContext.resources.getDimension(R.dimen._4sdp).toInt())
                } else if (type == 4) {
                    brightnessView.max=85
                    layoutParams.marginStart = mContext.resources.getDimension(R.dimen._2sdp).toInt()
                    containerLayout.setPadding(0,mContext.resources.getDimension(R.dimen._10sdp).toInt(),mContext.resources.getDimension(R.dimen._8sdp).toInt(), mContext.resources.getDimension(R.dimen._10sdp).toInt())
                }else if (type == 0) {
                    brightnessView.max=85
                    layoutParams.marginStart = mContext.resources.getDimension(R.dimen._32sdp).toInt()
                    containerLayout.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 6||type == 10) {
                    brightnessView.max=22
                    layoutParams.marginStart =mContext.resources.getDimension(R.dimen._21sdp).toInt()
                    containerLayout.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), 0,mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 9) {
                    brightnessView.max=85
                    iconView.scaleX=0.8f
                    iconView.scaleY=0.8f
                    layoutParams.marginStart = mContext.resources.getDimension(R.dimen._10sdp).toInt()
                    containerLayout.setPadding(0,mContext.resources.getDimension(R.dimen._12sdp).toInt(),mContext.resources.getDimension(R.dimen._8sdp).toInt(), mContext.resources.getDimension(R.dimen._12sdp).toInt())
                }
                nameView.layoutParams = layoutParams
                powerStateView.isChecked = item.parameters?.on == 1
                brightnessView.progress = item.parameters?.brightness?:20
            }
            -1 -> {
                if (type == 1) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._3sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                } else if (type == 2||type==3||type==7||type==8) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._16sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 0) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 6||type == 10) {
                    helper.itemView.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
                }else if (type == 9) {
                    helper.itemView.setPadding(0, mContext.resources.getDimension(R.dimen._11sdp).toInt(), mContext.resources.getDimension(R.dimen._8sdp).toInt(), mContext.resources.getDimension(R.dimen._11sdp).toInt())
                }
            }
        }
    }
}