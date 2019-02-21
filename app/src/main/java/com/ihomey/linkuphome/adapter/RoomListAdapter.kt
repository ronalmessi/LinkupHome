package com.ihomey.linkuphome.adapter


import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import cn.iclass.guideview.GuideBuilder
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.daimajia.swipe.SwipeLayout
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.data.entity.Room
import com.suke.widget.SwitchButton


/**
 * Created by dongcaizheng on 2018/4/11.
 */

class RoomListAdapter(layoutId: Int) : BaseQuickAdapter<Room, BaseViewHolder>(layoutId) {

    private lateinit var onCheckedChangeListener: OnCheckedChangeListener
    private lateinit var onSeekBarChangeListener: OnSeekBarChangeListener

    private var guide: Guide? = null

    var hasShowBindDeviceGuide by PreferenceHelper("hasShowBindDeviceGuide", false)

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = listener
    }

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.onSeekBarChangeListener = listener
    }

    private val icons = arrayListOf(R.mipmap.ic_zone_bed_room, R.mipmap.ic_zone_living_room, R.mipmap.ic_zone_dining_room, R.mipmap.ic_zone_kitchen, R.mipmap.ic_zone_bathroom, R.mipmap.ic_zone_balcony, R.mipmap.ic_zone_corridor, R.mipmap.ic_zone_entrance, R.mipmap.ic_zone_garage, R.mipmap.ic_zone_garden, R.mipmap.ic_zone_office, R.mipmap.ic_zone_bar_counter, R.mipmap.ic_zone_deck, R.mipmap.ic_zone_tv_wall, R.mipmap.ic_zone_other)

    override fun convert(helper: BaseViewHolder, item: Room) {

        helper.setText(R.id.tv_sub_zone_name, item.name)
        helper.setImageResource(R.id.iv_sub_zone_type, icons[item.type])
        val swipeLayout = helper.getView<SwipeLayout>(R.id.swipeLayout)
        swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        helper.addOnClickListener(R.id.btn_delete)
        helper.addOnClickListener(R.id.iv_color_cycling)
        helper.addOnClickListener(R.id.iv_lighting)
        helper.addOnClickListener(R.id.tv_sub_zone_name)

        if (item.sendTypes?.length == 1) helper.setGone(R.id.iv_color_cycling, true) else helper.setGone(R.id.iv_color_cycling, false)

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

        if (!hasShowBindDeviceGuide && helper.adapterPosition == 0) {
            helper.itemView.post { showGuideView(helper.itemView) }
        }
    }


    fun hideGuideView() {
        if (guide != null && guide?.isVisible!!) {
            guide?.dismiss()
        }
    }

    private fun showGuideView(view: View) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(mContext?.resources?.getDimension(R.dimen._6sdp)?.toInt()!!)
                .setHighTargetMarginTop(mContext?.resources?.getDimension(R.dimen._40sdp)?.toInt()!!)
                .setHighTargetPadding(mContext?.resources?.getDimension(R.dimen._2sdp)?.toInt()!!)
                .setOverlayTarget(false)
                .setOutsideTouchable(true)
        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {
                hasShowBindDeviceGuide = true
            }

            override fun onDismiss() {}
        })
        builder.addComponent(object : Component {
            override fun getView(inflater: LayoutInflater): View {
                return inflater.inflate(R.layout.view_guide_bind_device, null)
            }

            override fun getAnchor(): Int {
                return Component.ANCHOR_BOTTOM
            }

            override fun getFitPosition(): Int {
                return Component.FIT_CENTER
            }

            override fun getXOffset(): Int {
                return 0
            }

            override fun getYOffset(): Int {
                return mContext?.resources?.getDimension(R.dimen._8sdp)?.toInt()!!
            }

        })
        guide = builder.createGuide()
        guide?.setShouldCheckLocInWindow(true)
        guide?.show(mContext as Activity)
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(item: Room, isChecked: Boolean)
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(item: Room, progress: Int)
    }
}