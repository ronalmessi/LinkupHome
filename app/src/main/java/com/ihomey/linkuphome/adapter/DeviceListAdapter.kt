package com.ihomey.linkuphome.adapter


import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
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


    private val icons = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)

    override fun convert(helper: BaseViewHolder, item: SingleDevice) {
        helper.setText(R.id.tv_device_name, item.name)
        helper.setImageResource(R.id.iv_device_icon, icons[item.type])
        val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.tv_device_name)


        val cl_devices_item=swipeLayout.findViewById<ConstraintLayout>(R.id.cl_devices_item)
        if(item.type==4){
            cl_devices_item.setPadding( mContext.resources.getDimension(R.dimen._3sdp).toInt(),0,mContext.resources.getDimension(R.dimen._8sdp).toInt(),0)
        }else if(item.type==3){
            cl_devices_item.setPadding( mContext.resources.getDimension(R.dimen._16sdp).toInt(),0,mContext.resources.getDimension(R.dimen._8sdp).toInt(),0)
        }



        val sb_power = helper.getView<SwitchButton>(R.id.sb_power)
        sb_power.isChecked = item.state.on == 1
        sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }

        val seek_bar_brightness = helper.getView<SeekBar>(R.id.device_seek_bar_brightness)
        seek_bar_brightness.progress = item.state.brightness ?: 20

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