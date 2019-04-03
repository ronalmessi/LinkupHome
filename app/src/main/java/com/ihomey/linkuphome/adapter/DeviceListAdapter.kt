package com.ihomey.linkuphome.adapter


import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class DeviceListAdapter(layoutId: Int) : BaseQuickAdapter<SingleDevice, BaseViewHolder>(layoutId) {

    private lateinit var onCheckedChangeListener: OnCheckedChangeListener
    private lateinit var onSeekBarChangeListener: OnSeekBarChangeListener

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = listener
    }

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.onSeekBarChangeListener = listener
    }

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.name)
        val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.tv_device_name)
        val type=item.type-1
        helper.setImageResource(R.id.iv_device_icon, AppConfig.DEVICE_ICON[type])
        val cl_devices_item = swipeLayout.findViewById<ConstraintLayout>(R.id.cl_devices_item)
        val tv_device_name = cl_devices_item.findViewById<TextView>(R.id.tv_device_name)
        val layoutParams = tv_device_name.layoutParams as ViewGroup.MarginLayoutParams
        if (type == 0) {
            cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._3sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
        }else if (type == 1||type==2||type==6||type==7) {
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._15sdp).toInt()
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
            cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._16sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        }else if (type == 4) {
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._18sdp).toInt()
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
            cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._18sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        }else if (type == 5) {
            layoutParams.marginStart = mContext.resources.getDimension(R.dimen._8sdp).toInt()
            layoutParams.marginEnd = mContext.resources.getDimension(R.dimen._12sdp).toInt()
            cl_devices_item.setPadding(mContext.resources.getDimension(R.dimen._4sdp).toInt(), 0, mContext.resources.getDimension(R.dimen._8sdp).toInt(), 0)
        }
        tv_device_name.layoutParams = layoutParams

        val sb_power = helper.getView<SwitchButton>(R.id.sb_power)
        sb_power.isChecked = item.parameters?.on == 1
        sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }

        val seek_bar_brightness = helper.getView<SeekBar>(R.id.device_seek_bar_brightness)
        seek_bar_brightness.progress = item.parameters?.brightness?:20

        seek_bar_brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                onSeekBarChangeListener.onProgressChanged(item, seekBar.progress)
            }
        })
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: SingleDevice, isChecked: Boolean)
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(item: SingleDevice, progress: Int)
    }
}