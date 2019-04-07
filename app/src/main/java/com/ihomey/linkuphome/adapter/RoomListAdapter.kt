package com.ihomey.linkuphome.adapter


import android.widget.SeekBar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.AppConfig.Companion.ROOM_ICON
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.RoomAndDevices
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class RoomListAdapter(layoutId: Int) : BaseQuickAdapter<RoomAndDevices, BaseViewHolder>(layoutId) {

    private lateinit var onCheckedChangeListener: OnCheckedChangeListener
    private lateinit var onSeekBarChangeListener: OnSeekBarChangeListener


    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = listener
    }

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.onSeekBarChangeListener = listener
    }

    override fun convert(helper: BaseViewHolder, item: RoomAndDevices) {
        val room = item.room
        room?.let {
            helper.setText(R.id.tv_sub_zone_name, it.name)
            val type = it.type.minus(1)
            helper.setImageResource(R.id.iv_sub_zone_type, ROOM_ICON[type])
            val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
            helper.addOnClickListener(R.id.btn_delete)
            helper.addOnClickListener(R.id.iv_color_cycling)
            helper.addOnClickListener(R.id.iv_lighting)
            helper.addOnClickListener(R.id.tv_sub_zone_name)
            if (it.deviceTypes.length == 1) helper.setGone(R.id.iv_color_cycling, true) else helper.setGone(R.id.iv_color_cycling, false)

            val sb_power = helper.getView<SwitchButton>(R.id.sb_power)
            sb_power.isChecked = it.parameters?.on == 1
            sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }

            val seek_bar_brightness = helper.getView<SeekBar>(R.id.device_seek_bar_brightness)
            seek_bar_brightness.progress = it.parameters?.brightness ?: 20

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


    }


    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: RoomAndDevices, isChecked: Boolean)
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(item: RoomAndDevices, progress: Int)
    }


}