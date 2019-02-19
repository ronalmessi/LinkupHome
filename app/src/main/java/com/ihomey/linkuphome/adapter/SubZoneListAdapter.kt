package com.ihomey.linkuphome.adapter


import android.widget.SeekBar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.daimajia.swipe.SwipeLayout
import com.ihomey.linkuphome.data.vo.SingleDevice
import com.ihomey.linkuphome.data.vo.SubZoneModel
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class SubZoneListAdapter(layoutId: Int) : BaseQuickAdapter<SubZoneModel, BaseViewHolder>(layoutId) {

    private lateinit var onCheckedChangeListener: OnCheckedChangeListener
    private lateinit var onSeekBarChangeListener: OnSeekBarChangeListener

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = listener
    }

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.onSeekBarChangeListener = listener
    }
    private val icons = arrayListOf(R.mipmap.ic_zone_bed_room, R.mipmap.ic_zone_living_room, R.mipmap.ic_zone_dining_room,R.mipmap.ic_zone_kitchen,  R.mipmap.ic_zone_bathroom,R.mipmap.ic_zone_balcony, R.mipmap.ic_zone_corridor, R.mipmap.ic_zone_entrance, R.mipmap.ic_zone_garage, R.mipmap.ic_zone_garden, R.mipmap.ic_zone_office, R.mipmap.ic_zone_bar_counter, R.mipmap.ic_zone_deck, R.mipmap.ic_zone_tv_wall, R.mipmap.ic_zone_other)

    override fun convert(helper: BaseViewHolder, item: SubZoneModel) {

        helper.setText(R.id.tv_sub_zone_name, item.subZone?.device?.name)
        helper.setImageResource(R.id.iv_sub_zone_type, icons[item.subZone?.device?.type!!])
        val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.iv_color_cycling)
        helper.addOnClickListener(R.id.iv_lighting)
        helper.addOnClickListener(R.id.tv_sub_zone_name)

        if(item.subZone?.sendTypes?.length==1) helper.setGone(R.id.iv_color_cycling,true) else helper.setGone(R.id.iv_color_cycling,false)

        val sb_power = helper.getView<SwitchButton>(R.id.sb_power)
        sb_power.isChecked = item.subZone?.state?.on == 1
        sb_power.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener.onCheckedChanged(item, isChecked) }


        val seek_bar_brightness = helper.getView<SeekBar>(R.id.device_seek_bar_brightness)
        seek_bar_brightness.progress = item.subZone?.state?.brightness ?: 20

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
        fun onCheckedChanged(item: SubZoneModel, isChecked: Boolean)
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(item: SubZoneModel, progress: Int)
    }
}