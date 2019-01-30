package com.ihomey.linkuphome.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.data.vo.LampCategory

/**
 * Created by dongcaizheng on 2018/4/9.
 */
class UnAddedProductListAdapter(layoutId: Int) : BaseQuickAdapter<LampCategory, BaseViewHolder>(layoutId) {
    private val models = arrayListOf("C3", "R2","A2","N1","M1","V1","S1","S2","T1")
    private val names = arrayListOf(R.string.lamp_outdoor, R.string.lamp_rgb, R.string.lamp_cct, R.string.lamp_led_strip,R.string.lamp_bed,R.string.lamp_mini_outdoor,  R.string.lamp_s1, R.string.lamp_s2,R.string.lamp_down_light)
    private val icons = arrayListOf(R.mipmap.lamp_icon_lawn_unadded, R.mipmap.lamp_icon_rgb_unadded, R.mipmap.lamp_icon_warm_cold_unadded,R.mipmap.lamp_icon_led_unadded, R.mipmap.lamp_icon_bed_unadded,R.mipmap.lamp_icon_mini_outdoor_unadded,R.mipmap.lamp_icon_s_unadded, R.mipmap.lamp_icon_s_unadded,R.mipmap.lamp_icon_down_unadded)

    override fun convert(helper: BaseViewHolder?, item: LampCategory?) {
        helper?.setText(R.id.lamp_category_added_tv_model, models[item?.type!!])
        helper?.setText(R.id.lamp_category_added_tv_name, names[item?.type!!])
        if(item?.type==8){
            helper?.getView<ImageView>(R.id.lamp_category_added_iv_icon)?.scaleX=0.9f
            helper?.getView<ImageView>(R.id.lamp_category_added_iv_icon)?.scaleY=0.9f
        }
        helper?.setImageResource(R.id.lamp_category_added_iv_icon, icons[item?.type!!])
    }
}